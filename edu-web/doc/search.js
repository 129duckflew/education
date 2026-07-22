let api = [];
api.push({
    alias: 'api',
    order: '1',
    desc: '支付相关接口',
    link: '支付相关接口',
    list: []
})
api[0].list.push({
    order: '1',
    desc: '提供订单号发起支付',
});
api[0].list.push({
    order: '2',
    desc: '支付宝回调接口',
});
api.push({
    alias: 'AnswerController',
    order: '2',
    desc: '回答相关接口',
    link: '回答相关接口',
    list: []
})
api[1].list.push({
    order: '1',
    desc: '教授对问题发表回答或者修改已有的回答',
});
api[1].list.push({
    order: '2',
    desc: '给回答点赞/取消点赞',
});
api[1].list.push({
    order: '3',
    desc: '给回答添加收藏/取消收藏',
});
api[1].list.push({
    order: '4',
    desc: '根据用户id获取收藏的回答',
});
api[1].list.push({
    order: '5',
    desc: '根据回答id获取详情',
});
api.push({
    alias: 'ConsultAreaController',
    order: '3',
    desc: '咨询领域相关接口',
    link: '咨询领域相关接口',
    list: []
})
api[2].list.push({
    order: '1',
    desc: '获取所有顶层咨询领域',
});
api[2].list.push({
    order: '2',
    desc: '获取所有咨询领域(树形结构返回)',
});
api[2].list.push({
    order: '3',
    desc: '根据父节点获取子节点',
});
api[2].list.push({
    order: '4',
    desc: '根据领域id 获取领域内容(不包含子节点)',
});
api.push({
    alias: 'ConsultController',
    order: '4',
    desc: '咨询以及预约相关接口',
    link: '咨询以及预约相关接口',
    list: []
})
api.push({
    alias: 'FileController',
    order: '5',
    desc: '文件访问相关接口',
    link: '文件访问相关接口',
    list: []
})
api[4].list.push({
    order: '1',
    desc: '根据文件id获取文件,所有的文件访问都通过这个接口',
});
api[4].list.push({
    order: '2',
    desc: '教授简历上传,调用这个接口会上传头像文件返回文件id，获取id之后再调用指定的接口发生作用',
});
api[4].list.push({
    order: '3',
    desc: '上传学习资源',
});
api[4].list.push({
    order: '4',
    desc: '上传头像',
});
api.push({
    alias: 'HomeController',
    order: '6',
    desc: '主界面相关接口',
    link: '主界面相关接口',
    list: []
})
api[5].list.push({
    order: '1',
    desc: '前台用户登录',
});
api[5].list.push({
    order: '2',
    desc: '邮箱验证注册成为咨询者',
});
api[5].list.push({
    order: '3',
    desc: '手机验证码注册',
});
api[5].list.push({
    order: '4',
    desc: '获取绑定/注册邮箱验证码',
});
api[5].list.push({
    order: '5',
    desc: '获取邮箱是否被绑定',
});
api[5].list.push({
    order: '6',
    desc: '获取短信验证码',
});
api.push({
    alias: 'NewsController',
    order: '7',
    desc: '资讯相关接口',
    link: '资讯相关接口',
    list: []
})
api[6].list.push({
    order: '1',
    desc: '获取首页资讯',
});
api[6].list.push({
    order: '2',
    desc: '根据id获取资讯',
});
api.push({
    alias: 'OrderController',
    order: '8',
    desc: '订单相关接口',
    link: '订单相关接口',
    list: []
})
api[7].list.push({
    order: '1',
    desc: '分页获取用户订单',
});
api[7].list.push({
    order: '2',
    desc: '根据订单号查询订单',
});
api.push({
    alias: 'ProfessorController',
    order: '9',
    desc: '教授相关接口',
    link: '教授相关接口',
    list: []
})
api[8].list.push({
    order: '1',
    desc: '教授指定想要回答的问题的领域',
});
api[8].list.push({
    order: '2',
    desc: '根据咨询领域获取对应的教授列表',
});
api[8].list.push({
    order: '3',
    desc: '根据关键词搜索教授信息',
});
api[8].list.push({
    order: '4',
    desc: '修改个人简介',
});
api[8].list.push({
    order: '5',
    desc: '修改付费问答价格',
});
api[8].list.push({
    order: '6',
    desc: '根据教授id获取教授简介',
});
api[8].list.push({
    order: '7',
    desc: '对教授发布评价',
});
api[8].list.push({
    order: '8',
    desc: '查询教授评价',
});
api.push({
    alias: 'QuestionController',
    order: '10',
    desc: '咨询问题 相关接口',
    link: '咨询问题_相关接口',
    list: []
})
api[9].list.push({
    order: '1',
    desc: '用户发布问题',
});
api[9].list.push({
    order: '2',
    desc: '用户发布付费提问',
});
api[9].list.push({
    order: '3',
    desc: '根据问题id获取问题以及回答的列表',
});
api[9].list.push({
    order: '4',
    desc: '根据用户id获取提问',
});
api[9].list.push({
    order: '5',
    desc: '获取问答推荐',
});
api[9].list.push({
    order: '6',
    desc: '给问题点赞',
});
api.push({
    alias: 'SearchController',
    order: '11',
    desc: '搜索相关接口',
    link: '搜索相关接口',
    list: []
})
api[10].list.push({
    order: '1',
    desc: '关键词分页搜索问答',
});
api.push({
    alias: 'StudyGuideController',
    order: '12',
    desc: '学习导图相关接口',
    link: '学习导图相关接口',
    list: []
})
api[11].list.push({
    order: '1',
    desc: '获取推荐的学习导图',
});
api[11].list.push({
    order: '2',
    desc: '根据id获取学习导图详情',
});
api.push({
    alias: 'StudyResourceController',
    order: '13',
    desc: '学习资源相关接口',
    link: '学习资源相关接口',
    list: []
})
api[12].list.push({
    order: '1',
    desc: '发布学习资源',
});
api[12].list.push({
    order: '2',
    desc: '搜索学习资源',
});
api[12].list.push({
    order: '3',
    desc: '获取自己上传的所有资源',
});
api.push({
    alias: 'SystemMsgController',
    order: '14',
    desc: '系统消息接口',
    link: '系统消息接口',
    list: []
})
api[13].list.push({
    order: '1',
    desc: '获取自己的所有未读消息总数',
});
api[13].list.push({
    order: '2',
    desc: '根据消息类型获取类别下的所有消息',
});
api[13].list.push({
    order: '3',
    desc: '根据id获取消息详情',
});
api[13].list.push({
    order: '4',
    desc: '设置msgId消息已读',
});
api[13].list.push({
    order: '5',
    desc: '向某人发送私聊消息',
});
api[13].list.push({
    order: '6',
    desc: '获取私聊用户列表',
});
api[13].list.push({
    order: '7',
    desc: '获取和某人的聊天记录',
});
api[13].list.push({
    order: '8',
    desc: '打开与某个对象的聊天窗口',
});
api.push({
    alias: 'UserController',
    order: '15',
    desc: '用户相关接口',
    link: '用户相关接口',
    list: []
})
api[14].list.push({
    order: '1',
    desc: '获取个人信息',
});
api[14].list.push({
    order: '2',
    desc: '更改头像信息',
});
api[14].list.push({
    order: '3',
    desc: '上传简历信息',
});
api[14].list.push({
    order: '4',
    desc: '申请成为教授',
});
api[14].list.push({
    order: '5',
    desc: '用户选择感兴趣的领域',
});
api[14].list.push({
    order: '6',
    desc: '邮箱绑定',
});
api[14].list.push({
    order: '7',
    desc: '设置真名',
});
api[14].list.push({
    order: '8',
    desc: '设置身份证号',
});
api[14].list.push({
    order: '9',
    desc: '设置用户名',
});
api[14].list.push({
    order: '10',
    desc: '修改性别',
});
api[14].list.push({
    order: '11',
    desc: '绑定手机号',
});
api[14].list.push({
    order: '12',
    desc: '查询用户是否在线',
});
api[14].list.push({
    order: '13',
    desc: '根据用户id获取头像',
});
api[14].list.push({
    order: '14',
    desc: '重置密码',
});
api[14].list.push({
    order: '15',
    desc: '获取用户最后一次登录的记录 如果为空则是第一次登录系统',
});
document.onkeydown = keyDownSearch;
function keyDownSearch(e) {
    const theEvent = e;
    const code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code == 13) {
        const search = document.getElementById('search');
        const searchValue = search.value;
        let searchArr = [];
        for (let i = 0; i < api.length; i++) {
            let apiData = api[i];
            const desc = apiData.desc;
            if (desc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                searchArr.push({
                    order: apiData.order,
                    desc: apiData.desc,
                    link: apiData.link,
                    alias: apiData.alias,
                    list: apiData.list
                });
            } else {
                let methodList = apiData.list || [];
                let methodListTemp = [];
                for (let j = 0; j < methodList.length; j++) {
                    const methodData = methodList[j];
                    const methodDesc = methodData.desc;
                    if (methodDesc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                        methodListTemp.push(methodData);
                        break;
                    }
                }
                if (methodListTemp.length > 0) {
                    const data = {
                        order: apiData.order,
                        desc: apiData.desc,
                        alias: apiData.alias,
                        link: apiData.link,
                        list: methodListTemp
                    };
                    searchArr.push(data);
                }
            }
        }
        let html;
        if (searchValue == '') {
            const liClass = "";
            const display = "display: none";
            html = buildAccordion(api,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        } else {
            const liClass = "open";
            const display = "display: block";
            html = buildAccordion(searchArr,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        }
        const Accordion = function (el, multiple) {
            this.el = el || {};
            this.multiple = multiple || false;
            const links = this.el.find('.dd');
            links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
        };
        Accordion.prototype.dropdown = function (e) {
            const $el = e.data.el;
            $this = $(this), $next = $this.next();
            $next.slideToggle();
            $this.parent().toggleClass('open');
            if (!e.data.multiple) {
                $el.find('.submenu').not($next).slideUp("20").parent().removeClass('open');
            }
        };
        new Accordion($('#accordion'), false);
    }
}

function buildAccordion(apiData, liClass, display) {
    let html = "";
    let doc;
    if (apiData.length > 0) {
         for (let j = 0; j < apiData.length; j++) {
            html += '<li class="'+liClass+'">';
            html += '<a class="dd" href="' + apiData[j].alias + '.html#header">' + apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
            html += '<ul class="sectlevel2" style="'+display+'">';
            doc = apiData[j].list;
            for (let m = 0; m < doc.length; m++) {
                html += '<li><a href="' + apiData[j].alias + '.html#_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + doc[m].desc + '</a> </li>';
            }
            html += '</ul>';
            html += '</li>';
        }
    }
    return html;
}