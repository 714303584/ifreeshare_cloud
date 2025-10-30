package com.ifreeshare.user.service;

import com.ifreeshare.user.entity.IsUsers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (IsUsers)表服务接口
 *
 * @author 
 * @since 2023-10-15 11:47:12
 */
public interface IsUsersService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    IsUsers queryById(Long id);

    /**
     * 分页查询
     *
     * @param isUsers 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<IsUsers> queryByPage(IsUsers isUsers, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param isUsers 实例对象
     * @return 实例对象
     */
    IsUsers insert(IsUsers isUsers);

    /**
     * 修改数据
     *
     * @param isUsers 实例对象
     * @return 实例对象
     */
    IsUsers update(IsUsers isUsers);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);


    /**
     * 登陆用
     * @param loginName
     * @return
     */
    IsUsers queryByLoginName(String loginName);

}
