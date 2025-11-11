# gitlab

## 登录
默认用户是root，默认密码使用命令查看
```Bash
docker exec -it gitlab grep 'Password:' /etc/gitlab/initial_root_password
```

## 创建组，项目


## 配置ssh


## 配置远程仓库
git remove -v
git remote add gitlab git@localhost:home/sprival.git


## 推送代码
git push gitlab main

## 配置gitlab-runner
创建runner


## 设置上传大小
Settings > CI/CD > General pipelines。
Maximum artifacts size (MB)

## 配置docker用户名密码


