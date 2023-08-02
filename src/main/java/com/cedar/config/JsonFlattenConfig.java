package com.cedar.config;

import com.github.wnameless.json.base.GsonJsonCore;
import com.github.wnameless.json.base.JsonCore;
import com.github.wnameless.json.flattener.FlattenMode;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.flattener.JsonFlattenerFactory;
import com.github.wnameless.json.flattener.PrintMode;
import com.github.wnameless.json.unflattener.JsonUnflattener;
import com.github.wnameless.json.unflattener.JsonUnflattenerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class JsonFlattenConfig {

    @Bean
    public JsonFlattenerFactory jsonFlattenerFactory(){
        Consumer<JsonFlattener> configure = jf-> jf.withPrintMode(PrintMode.PRETTY);
        JsonCore<?> jsonCore = new GsonJsonCore();

        return new JsonFlattenerFactory(configure, jsonCore);
    }

    @Bean
    public JsonUnflattenerFactory jsonUnflattenerFactory(){
        Consumer<JsonUnflattener> configure = ju-> ju.withFlattenMode(FlattenMode.MONGODB);
        JsonCore<?> jsonCore = new GsonJsonCore();

        return new JsonUnflattenerFactory(configure, jsonCore);
    }

}
