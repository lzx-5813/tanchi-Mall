# tanchi-Mall
贪吃商城
项目介绍：
基于Spring Boot+Redis的店捕点评APP,实现了找店铺=>写点评=>看热评=>点赞关注=>
关注Feed流的完整业务流程。
主要工作：
1.短信登录：使用Redis实现分布式Session,解决集群间登录态同步问题；使用Hash代替
Stig来存储用户信息，节约了x%的内存并便于单字段的修改。（需要自己实际测试对比数
据，节省内存的原因是不用保存序列化对象信息或者JSON的一些额外字符串)
2.店铺查询：使用Rdis对高频访问店铺进行缓存，降低DB压力同时提升90%的数据查询性
能。
3.为方便其他业务后续使用缓存，使用泛型+函数式编程实现了通用缓存访问静态方法，并解决
了袋存雪崩、缓存穿透等问题。
4.使用常量类全局管理Redis Key前缀、TTL等，保证了键空间的业务隔离，减少冲突。
5.使用Redis的Geo+Hash数据结构分类存储附近商户，并使用Geo Search命令实现高性能
商户查询及按距离排序。
6.使用Redis List数据结构存储用户点赞信息，并基于ZSet实现TopN点赞排行，实测相对于
DB查询性能提升x%。(需要自己实际测试对比数据)
7.使用Redis Set数据结构实现用户关注、共同关注功能（交集），实测相对于DB查询性能提
升x%。(需要自己实际测试对比数据)
8.使用Redis BitMap实现用户连续签到统计功能，相对于传统关系库存储，节约x%的内存并
提升x%的查询性能。（需要自己实际测试对比数据）
9.在系统用户量不大的前提下，基于推模式实现关注Fed流，保证了新点评消息的及时可达，
并减少用户访问的等待时间。
10.优惠券秒杀：使用Redis+Lua脚本实现库存预检，并通过Stream队列实现订单的异步创
建，解决了超卖问题、实现一人一单。实现相比传统数据库，秒杀性能提高了x%。(需要自
己实际测试对比数据)