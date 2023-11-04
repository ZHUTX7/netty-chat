package com.zzz.pro.service;

import com.zzz.pro.enums.ResultEnum;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.mapper.UserTagMapper;
import com.zzz.pro.controller.form.DeleteTagForm;
import com.zzz.pro.controller.form.UserTagForm;
import com.zzz.pro.controller.vo.UserTagVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserTagService {
    @Resource
    private UserTagMapper userTagMapper;

    //根据user_id插锁所有user_tag
    public List<UserTagVO> queryUserTag(String userId){
        try{
            List<Map> list =   userTagMapper.queryTagByUserId(userId);
            if(list == null || list.size() == 0){
                return null;
            }

            return  list.stream().map(e->{
                UserTagVO vo = new UserTagVO();
                vo.setUserKey(e.get("user_key").toString());
                vo.setUserValue(e.get("user_value").toString());
                vo.setTag(e.get("tag").toString());
                return vo;
            }).collect(Collectors.toList());

        }catch (Exception e){
            e.printStackTrace();
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(),"数据错误");
        }
    }

    public void batchInsertOrUpdateUserTag(UserTagForm form){
        try{
            if(CollectionUtils.isEmpty(form.getDataList()))
                return;
            userTagMapper.insertOrUpdateTag(form.getUserId(),form.getDataList());
        }catch (Exception e){
            e.printStackTrace();
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(),"数据错误");
        }
    }

    public void clearUserTag(DeleteTagForm form) {
        try{
            userTagMapper.deleteByUserIdAndUserKeyIn(form.getUserId(), form.getKeys());
        }catch (Exception e){
            e.printStackTrace();
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(),"数据错误");
        }
    }
}
