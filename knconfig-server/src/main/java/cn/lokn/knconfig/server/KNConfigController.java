package cn.lokn.knconfig.server;

import cn.lokn.knconfig.server.dal.ConfigsMapper;
import cn.lokn.knconfig.server.model.Configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: lokn
 * @date: 2024/05/09 08:04
 */
@RestController
public class KNConfigController {

    @Autowired
    ConfigsMapper mapper;

    @Autowired
    DistributedLocks distributedLocks;

    Map<String, Long> VERSIONS = new HashMap<>();

    @GetMapping("/list")
    public List<Configs> list(String app, String env, String ns) {
        return mapper.list(app, env, ns);
    }

    @PostMapping("/update")
    public List<Configs> update(@RequestParam("app") String app,
                                @RequestParam("env") String env,
                                @RequestParam("ns") String ns,
                                @RequestBody Map<String, String> params) {
        params.forEach((k, v) -> {
            insertOrUpdate(new Configs(app, env, ns, k, v));
        });
        VERSIONS.put(app + "-" + env + "-" + ns, System.currentTimeMillis());
        return mapper.list(app, env, ns);
    }

    private void insertOrUpdate(Configs configs) {
        Configs conf = mapper.select(configs.getApp(), configs.getEnv(),
                configs.getNs(), configs.getPkey());
        if (conf == null) {
            mapper.insert(configs);
        } else {
            mapper.update(configs);
        }
    }

    @GetMapping("/version")
    public long version(String app, String env, String ns) {
        return VERSIONS.getOrDefault(app + "-" + env + "-" + ns , -1L);
    }

    @GetMapping("/status")
    public boolean status() {
        List<String> s = new ArrayList<>();
        return distributedLocks.getLocked().get();
    }

}
