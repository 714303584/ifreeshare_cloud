package com.ifreeshare.user.dao;

import com.ifreeshare.user.entity.IsUsers;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * (IsUsers)表数据库访问层
 *
 * @author 
 * @since 2023-10-15 11:47:09
 */
public interface IsUsersDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    IsUsers queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param isUsers 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<IsUsers> queryAllByLimit(IsUsers isUsers, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param isUsers 查询条件
     * @return 总行数
     */
    long count(IsUsers isUsers);

    /**
     * 新增数据
     *
     * @param isUsers 实例对象
     * @return 影响行数
     */
    int insert(IsUsers isUsers);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<IsUsers> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<IsUsers> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<IsUsers> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<IsUsers> entities);

    /**
     * 修改数据
     *
     * @param isUsers 实例对象
     * @return 影响行数
     */
    int update(IsUsers isUsers);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据loginName进行登陆
     * @param loginName
     * @return
     */
    IsUsers queryByLoginName(@Param("login_name") String loginName);

}

