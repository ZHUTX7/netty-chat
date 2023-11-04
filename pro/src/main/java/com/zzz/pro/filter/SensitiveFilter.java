package com.zzz.pro.filter;


import com.zzz.pro.enums.MsgTypeEnum;
import com.zzz.pro.netty.enity.ChatMsg;
import com.zzz.pro.service.api.ContentAnalyseService;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveFilter {

    @Value("${sensitive-words.path}")
    private String PATH;

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();


    //文字内容检测引擎初始化
    @PostConstruct
    public void init()  {
        logger.info("-------------初始化敏感词库-------------");
        File file = null;
        InputStream is = null;
        BufferedReader reader = null;
        try {
            file = ResourceUtils.getFile(PATH);
            is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
            // 处理文件操作
            String keyword;
            while ((keyword = reader.readLine()) != null){
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("敏感词文件未找到：");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("加载敏感词文件失败：" );
        } finally {
            // 关闭流
            if(reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        logger.info("-------------敏感词库初始化完成-------------");
    }

    // 将一个敏感词添加到前缀树中
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null){
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指向子节点，进入下一轮循环
            tempNode = subNode;

            // 设置结束标识
            if (i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text  待过滤的文本
     * @return  过滤后的文本
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

        // 指针1 指向前缀树
        TrieNode tempNode = rootNode;
        // 指针2 指向文本
        int begin = 0;
        // 指针3 指针文本
        int position = 0;

        // 结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()){
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)){
                // 若指针1 处于根节点,将此符号计入结果，让指针2向下走一步
                if (tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()){
                // 发现敏感词，将 begin ~ position 字符串替换掉

                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                ++position;
            }

        }

        // 将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    /**
     * 过滤敏感词
     * @param text  待过滤的文本
     * @return  过滤后的文本
     */
    public Boolean isSensitiveText(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

        // 指针1 指向前缀树
        TrieNode tempNode = rootNode;
        // 指针2 指向文本
        int begin = 0;
        // 指针3 指针文本
        int position = 0;

        // 结果

        while (position < text.length()){
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)){
                // 若指针1 处于根节点,将此符号计入结果，让指针2向下走一步
                if (tempNode == rootNode){
                    begin++;
                }
                // 无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                // 以begin开头的字符串不是敏感词
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()){
                return true;
            } else {
                // 检查下一个字符
                ++position;
            }

        }
        return false;
    }

    // 判断是否是特殊符号
    private boolean isSymbol(Character c){
        // 0x2E80 ~ 0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    // 前缀树
    private class TrieNode{

        // 子节点(key是下级字符, value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
    }
}