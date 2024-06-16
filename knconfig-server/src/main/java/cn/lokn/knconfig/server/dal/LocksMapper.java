package cn.lokn.knconfig.server.dal;

import org.apache.ibatis.annotations.Select;

/**
 * mapper for dist locks.
 */
public interface LocksMapper {

    @Select("select app from locks where id = 1 from update")
    String selectForUpdate();

}
