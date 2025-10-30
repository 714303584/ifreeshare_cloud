package com.ifreeshare.user.service.impl;

import com.ifreeshare.user.entity.IsUsers;
import com.ifreeshare.user.dao.IsUsersDao;
import com.ifreeshare.user.service.IsUsersService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


/**
 * (IsUsers)表服务实现类
 *
 * @author 
 * @since 2023-10-15 11:47:13
 */
@Service("isUsersService")
public class IsUsersServiceImpl implements IsUsersService {

    public static final String user_redis_key ="ifreeshare:user:";

    @Autowired
    private IsUsersDao isUsersDao;

    @Autowired
    private RedissonClient redisson;

    public String getRedisKey(Long id){
        return user_redis_key+id;
    }

    /**
     * 获取redison桶
     * @param id
     * @return
     */
    public RBucket<IsUsers> getBucket(Long id){
        return redisson.getBucket(getRedisKey(id));
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public IsUsers queryById(Long id) {
        RBucket<IsUsers> isUsersRBucket = redisson.getBucket(getRedisKey(id));
        IsUsers isUsers = isUsersRBucket.get();
        if(isUsers == null){
            isUsers = this.isUsersDao.queryById(id);
            isUsersRBucket.set(isUsers);
        }
        return isUsers;
    }

    /**
     * 分页查询
     *
     * @param isUsers 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<IsUsers> queryByPage(IsUsers isUsers, PageRequest pageRequest) {
        long total = this.isUsersDao.count(isUsers);
        return new PageImpl<>(this.isUsersDao.queryAllByLimit(isUsers, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param isUsers 实例对象
     * @return 实例对象
     */
    @Override
    public IsUsers insert(IsUsers isUsers) {
        this.isUsersDao.insert(isUsers);
        return isUsers;
    }

    /**
     * 修改数据
     *
     * @param isUsers 实例对象
     * @return 实例对象
     */
    @Override
    public IsUsers update(IsUsers isUsers) {
        this.isUsersDao.update(isUsers);
        //更新清楚缓存
        getBucket(isUsers.getId()).delete();
        return this.queryById(isUsers.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        getBucket(id).delete();
        return this.isUsersDao.deleteById(id) > 0;
    }

    @Override
    public IsUsers queryByLoginName(String loginName){
        return this.isUsersDao.queryByLoginName(loginName);
    }
}
