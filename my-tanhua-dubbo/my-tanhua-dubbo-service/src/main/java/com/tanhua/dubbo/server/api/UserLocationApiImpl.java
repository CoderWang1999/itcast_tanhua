package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.tanhua.dubbo.server.pojo.UserLocation;
import com.tanhua.dubbo.server.vo.UserLocationVo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * @author ZJWzxy
 */
@Service(version = "1.0.0")
public class UserLocationApiImpl implements UserLocationApi {


    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 地理位置
     * @param userId 用户id
     * @param longitude 经纬度
     * @param latitude 经纬度
     * @param address 地址
     * @return
     */
    @Override
    public String updateUserLocation(Long userId, Double longitude, Double latitude, String address) {

        //创建地址对象
        UserLocation userLocation = new UserLocation();
        //填充信息
        userLocation.setAddress(address);
        userLocation.setLocation(new GeoJsonPoint(longitude, latitude));
        userLocation.setUserId(userId);
        //设置查询条件
        Query query = Query.query(Criteria.where("userId").is(userLocation.getUserId()));
        //通过MonGoDB查询
        UserLocation ul = this.mongoTemplate.findOne(query, UserLocation.class);
        if (ul == null) {
            //新增
            userLocation.setId(ObjectId.get());
            userLocation.setCreated(System.currentTimeMillis());
            userLocation.setUpdated(userLocation.getCreated());
            userLocation.setLastUpdated(userLocation.getCreated());

            this.mongoTemplate.save(userLocation);

            return userLocation.getId().toHexString();
        } else {
            //更新
            Update update = Update
                    .update("location", userLocation.getLocation())
                    .set("updated", System.currentTimeMillis())
                    .set("lastUpdated", ul.getUpdated());
            this.mongoTemplate.updateFirst(query, update, UserLocation.class);
        }

        return ul.getId().toHexString();

    }

    /**
     * 查询用户地理位置
     * @param userId 用户id
     * @return UserLocationVo
     */
    @Override
    public UserLocationVo queryByUserId(Long userId) {
        //设置查询条件
        Query query=Query.query(Criteria.where("userId").is(userId));
        UserLocation userLocation = this.mongoTemplate.findOne(query, UserLocation.class);
        //判断
        if (null!=userLocation){
            //查寻成功
            return UserLocationVo.format(userLocation);
        }
        //查询失败
        return null;
    }

    /**
     * 根据地理位置查询用户
     * @param longitude 经度
     * @param latitude 纬度
     * @param range 范围
     * @return UserLocationVo
     */
    @Override
    public List<UserLocationVo> queryUserFromLocation(Double longitude, Double latitude, Integer range) {
        //中心点
        GeoJsonPoint geoJsonPoint = new GeoJsonPoint(longitude, latitude);

        //转换为2dsphere的距离
        Distance distance = new Distance(range / 1000, Metrics.KILOMETERS);

        //画一个圆
        Circle circle = new Circle(geoJsonPoint, distance);
        Query query = Query.query(Criteria.where("location").withinSphere(circle));
        return UserLocationVo.formatToList(this.mongoTemplate.find(query, UserLocation.class));
    }
}
