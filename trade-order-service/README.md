#  trade-order



## 说明

* 项目编码：UTF-8
* JDK:1.8
* IDE：不限
* 项目开发方式  
  - 分支开发，发布时合并主干
  - 环境隔离（profile：local、dev、uat、prod）

## 模块

* biz：公共业务模块（供其它模块调用）<br/>
* biz-base:基础业务 <br/>
* biz-req-trace:请求链路跟踪<br/>
* biz-glue:<br/>
* trade-order-api：业务代码 <br/>
* trade-order-gateway：路由服务 <br/>


###  代码提交规范

#### 提交的原则

* 保持较小的提交粒度，一次提交只做一件事情。提交的粒度可以是一个小功能点或者一个bugfix。更细粒度的提交便于追踪bug，撤回具体的改动比撤回一大块改动更容易。
* 提交记录一定要明确，避免大量重复及语焉不详。
* 无直接关联的文件不要在同一次提交。
* 只有编译通过的代码才可以提交。
* 每次提交代码，都要写Commit message（提交说明），认真对待提交备注，很有可能以后看备注的人是你自己。
* git push无须过于频繁。不要每提交一次就推送一次，多积攒几个提交后再推送，这样可以避免在进行一次提交后发现代码中还有小错误。毕竟git push之后要再撤销公共分支的代码，还是要麻烦一些。
* 功能需求仅一个人进行开发时，在功能完成之前不要着急创建远程分支。


#### commit message前缀

```
提交规则：

feature或feat:新功能
add : 新增相关内容
fix:修复bug
docs:文档添加、修改，如README, CHANGELOG。
style:格式（不影响代码运行的变动,如格式化，缩进等）
refactor:重构（即不是新增功能，也不是修改bug的代码变动）
test:增加测试
chore:构建过程或辅助工具的变动(如package.sh)
deps:依赖变更（比如guava版本变更)
revert:撤销以前的commit(必须写清楚)
log:增加、调整log输出等
perf:性能优化
config:配置文件修改（如第三方接口url调整）
remove:移除
experience:体验优化
ui:纯粹CSS样式变动，不影响功能代码
other:其他原因，如上述不能覆盖，才用。如：合并代码，解决代码冲突等

eg：
refactor：优化调用链
```
