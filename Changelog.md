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
