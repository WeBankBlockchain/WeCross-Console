### v1.0.0

(2020-12-17)

**新增**

* 账户相关命令：registerAccount、login、logout、addChainAccount、setDefaultAccount、listAccount
* 新增事务命令：invoke，根据上下文自动选择发交易的方式
* 新增合约： 桥接合约(WeCrossHub)、资产示例合约(AssetSample)
* 文件名补全：部署相关命令支持文件名补全

**更改**
* 删除获取账户列表的命令：listAccounts
* 资源调用相关命令参数列表去掉账户名
* 部分事务命令重命名：
    * getTransactionIDs => getXATransaction
    * getTransactionIDs => listXATransactions

### v1.0.0-rc4

(2020-08-18)

**新增**

* 资源部署命令
  * FISCO BCOS：bcosDeploy、bcosRegister
  * Fabric：fabricInstall、fabricInstantiate、fabricUpgrade
* 2PC事务操作命令
  * 操作：startTransaction、execTransaction、callTransaction、commitTransaction、rollbackTransaction
  * 查询：getTransactionInfo、getTransactionIDs
* 跨链资源集
  * 一般示例代码：HelloWorld.sol、sacc.go、fabcar.java
  * HTLC合约代码：HTLC.sol、htlc.go等
  * 两阶段示例：EvidenceSample.sol、EvidenceSample.go等

### v1.0.0-rc3

(2020-06-15)

**更改**

* 适配v1.0.0-rc3的wecross-java-sdk
* 下载脚本稳定性修复

### v1.0.0-rc2

(2020-05-12)

**新增**

* 安全通讯：控制台和Router之间采用TLS协议通讯
* 增加命令：
  * detail：查看资源详情信息
  * supportedStubs：查看连接Router支持的Stub插件列表
  * listAccounts：查看Router配置的账户列表
  * genSecretAndHash：跨链转账辅助命令，生成一个秘密和它的哈希
  * genTimelock：跨链转账辅助命令，生成两个合法的时间戳
  * newContract：创建一个基于哈希时间锁合约的跨链转账合同

**更改**

* 合约调用：合约调用需要指定签名的账户
* 命令更新：合约调用不再需要指定返回值类型列表，因此删除了相关衍生命令
* 配置文件：更新配置文件为toml格式，新增TLS配置项

### v1.0.0-rc1

(2019-12-30)

**功能**

* 跨连控制台基本功能

``` 
=============================================================================================
Welcome to WeCross console(v1.0.0-rc1)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.

=============================================================================================
[server1]> -h
Error: unsupported command.

[server1]> help
---------------------------------------------------------------------------------------------
quit                               Quit console.
currentServer                      Show currently connected WeCross server.
listServers                        List all configured WeCross servers.
switch                             Switch to a specific WeCross server.
listLocalResources                 List local resources configured by WeCross server.
listResources                      List all resources including remote resources.
status                             Check if the resource exists.
getData                            Get data from contract.
setData                            Set data for contract.
call                               Call constant method of smart contract.
callInt                            Call constant method of smart contract with int returned.
callIntArray                       Call constant method of smart contract with int array returned.
callString                         Call constant method of smart contract with string returned.
callStringArray                    Call constant method of smart contract with string array returned.
sendTransaction                    Call non-constant method of smart contract.
sendTransactionInt                 Call non-constant method of smart contract with int returned.
sendTransactionIntArray            Call non-constant method of smart contract with int array returned.
sendTransactionString              Call non-constant method of smart contract with string returned.
sendTransactionStringArray         Call non-constant method of smart contract with string array returned.
WeCross.getResource                Init resource by path, and assign it to a custom variable.
[resource].[command]               Equal to command: command [path].

---------------------------------------------------------------------------------------------
```



**框架**

* 适配WeCross跨连路由v1.0.0-rc1版本
* 集成WeCross Java SDK v1.0.0-rc1版本
