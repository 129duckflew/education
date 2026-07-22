let api = [];
api.push({
    alias: 'api',
    order: '1',
    desc: '后台管理: 管理员用户管理',
    link: '后台管理:_管理员用户管理',
    list: []
})
api[0].list.push({
    order: '1',
    desc: '添加管理员',
});
api[0].list.push({
    order: '2',
    desc: '获取所有管理员信息',
});
api[0].list.push({
    order: '3',
    desc: '修改管理员信息',
});
api[0].list.push({
    order: '4',
    desc: '给管理员添加角色',
});
api[0].list.push({
    order: '5',
    desc: '获取管理员的角色',
});
api[0].list.push({
    order: '6',
    desc: '修改管理员角色',
});
api[0].list.push({
    order: '7',
    desc: '根据真名或者昵称来搜搜',
});
api[0].list.push({
    order: '8',
    desc: '根据username搜索',
});
api.push({
    alias: 'AdminHomeController',
    order: '2',
    desc: '管理员主界面相关接口',
    link: '管理员主界面相关接口',
    list: []
})
api[1].list.push({
    order: '1',
    desc: '获取当前管理员用户的菜单列表',
});
api[1].list.push({
    order: '2',
    desc: '获取自己的个人资料',
});
api[1].list.push({
    order: '3',
    desc: '修改自己的个人资料',
});
api[1].list.push({
    order: '4',
    desc: '后台登录 登录之后返回token',
});
api[1].list.push({
    order: '5',
    desc: '获取当前在线用户数',
});
api[1].list.push({
    order: '6',
    desc: '获取时间段内的活跃数',
});
api[1].list.push({
    order: '7',
    desc: '获取时间段内的提问数量',
});
api[1].list.push({
    order: '8',
    desc: '获取时间段内的回答数量',
});
api.push({
    alias: 'AnswerController',
    order: '3',
    desc: '后台管理:回答相关接口',
    link: '后台管理:回答相关接口',
    list: []
})
api[2].list.push({
    order: '1',
    desc: '对回答进行封禁',
});
api[2].list.push({
    order: '2',
    desc: '根据关键词搜索回答',
});
api[2].list.push({
    order: '3',
    desc: '根据id获取回答',
});
api[2].list.push({
    order: '4',
    desc: '分页获取回答',
});
api.push({
    alias: 'BaseUserController',
    order: '4',
    desc: '后台管理:用户相关接口',
    link: '后台管理:用户相关接口',
    list: []
})
api[3].list.push({
    order: '1',
    desc: '禁用账户',
});
api[3].list.push({
    order: '2',
    desc: '搜索用户',
});
api[3].list.push({
    order: '3',
    desc: '修改用户信息',
});
api.push({
    alias: 'ConsultAreaController',
    order: '5',
    desc: '后台管理: 咨询领域相关接口',
    link: '后台管理:_咨询领域相关接口',
    list: []
})
api[4].list.push({
    order: '1',
    desc: '获取所有咨询领域',
});
api[4].list.push({
    order: '2',
    desc: '添加咨询领域',
});
api[4].list.push({
    order: '3',
    desc: '修改领域名称',
});
api[4].list.push({
    order: '4',
    desc: '删除领域',
});
api.push({
    alias: 'DegreeController',
    order: '6',
    desc: '学位相关接口',
    link: '学位相关接口',
    list: []
})
api[5].list.push({
    order: '1',
    desc: '获取所有学位',
});
api.push({
    alias: 'EduDataSourceController',
    order: '7',
    desc: '后台管理:学信网数据相关接口',
    link: '后台管理:学信网数据相关接口',
    list: []
})
api[6].list.push({
    order: '1',
    desc: '通过用户id查询学信网数据',
});
api[6].list.push({
    order: '2',
    desc: '通过身份证查询学信网数据',
});
api[6].list.push({
    order: '3',
    desc: '添加数据源',
});
api[6].list.push({
    order: '4',
    desc: '为某个人的数据源添加教育经历',
});
api[6].list.push({
    order: '5',
    desc: '删除数据源',
});
api[6].list.push({
    order: '6',
    desc: '分页获取学信网数据源',
});
api.push({
    alias: 'FileController',
    order: '8',
    desc: '管理员后台:文件访问相关接口',
    link: '管理员后台:文件访问相关接口',
    list: []
})
api[7].list.push({
    order: '1',
    desc: '上传资讯文章封面',
});
api[7].list.push({
    order: '2',
    desc: '上传管理员头像',
});
api[7].list.push({
    order: '3',
    desc: '根据文件id获取文件,所有的文件访问都通过这个接口',
});
api.push({
    alias: 'JobRankController',
    order: '9',
    desc: '职称相关接口',
    link: '职称相关接口',
    list: []
})
api[8].list.push({
    order: '1',
    desc: '获取所有职称',
});
api.push({
    alias: 'MenuController',
    order: '10',
    desc: '后台管理: 菜单相关接口',
    link: '后台管理:_菜单相关接口',
    list: []
})
api[9].list.push({
    order: '1',
    desc: '获取所有菜单以及菜单需要的权限',
});
api.push({
    alias: 'NewsController',
    order: '11',
    desc: '后台管理:资讯相关接口',
    link: '后台管理:资讯相关接口',
    list: []
})
api[10].list.push({
    order: '1',
    desc: '分页获取所有资讯',
});
api[10].list.push({
    order: '2',
    desc: '更新资讯信息',
});
api[10].list.push({
    order: '3',
    desc: '添加资讯',
});
api[10].list.push({
    order: '4',
    desc: '根据id删除资讯',
});
api.push({
    alias: 'ProfessorController',
    order: '12',
    desc: '后台管理:教授相关接口',
    link: '后台管理:教授相关接口',
    list: []
})
api[11].list.push({
    order: '1',
    desc: '分页获取所有待审核教授',
});
api[11].list.push({
    order: '2',
    desc: '通过教授审核',
});
api[11].list.push({
    order: '3',
    desc: '添加教授简介(已实现)',
});
api[11].list.push({
    order: '4',
    desc: '设置教授职称(已实现)',
});
api[11].list.push({
    order: '5',
    desc: '删除教授资格',
});
api.push({
    alias: 'QuestionController',
    order: '13',
    desc: '后台管理:问题相关接口',
    link: '后台管理:问题相关接口',
    list: []
})
api[12].list.push({
    order: '1',
    desc: '获取不同状态的问题',
});
api[12].list.push({
    order: '2',
    desc: '管理员对问题进行审核/禁用',
});
api[12].list.push({
    order: '3',
    desc: '根据关键词搜索问题',
});
api.push({
    alias: 'ResearchDirectionController',
    order: '14',
    desc: '研究方向相关接口',
    link: '研究方向相关接口',
    list: []
})
api.push({
    alias: 'RoleController',
    order: '15',
    desc: '后台管理:角色相关接口',
    link: '后台管理:角色相关接口',
    list: []
})
api[14].list.push({
    order: '1',
    desc: '添加角色',
});
api[14].list.push({
    order: '2',
    desc: '根据id删除角色',
});
api[14].list.push({
    order: '3',
    desc: '更新角色信息',
});
api[14].list.push({
    order: '4',
    desc: '获取所有角色不附带菜单信息',
});
api[14].list.push({
    order: '5',
    desc: '获取所有角色(不包含超级管理员)以及关联的菜单id',
});
api.push({
    alias: 'StudyGuideController',
    order: '16',
    desc: '后台管理:学习导图相关接口',
    link: '后台管理:学习导图相关接口',
    list: []
})
api[15].list.push({
    order: '1',
    desc: '根据id查询学习导图详情',
});
api[15].list.push({
    order: '2',
    desc: '分页查询学习导图',
});
api[15].list.push({
    order: '3',
    desc: '修改学习导图',
});
api[15].list.push({
    order: '4',
    desc: '添加学习导图',
});
api[15].list.push({
    order: '5',
    desc: '添加导图节点',
});
api[15].list.push({
    order: '6',
    desc: '删除学习导图节点',
});
api[15].list.push({
    order: '7',
    desc: '根据导图id获取关联的领域',
});
api.push({
    alias: 'UniversityController',
    order: '17',
    desc: '学校相关接口',
    link: '学校相关接口',
    list: []
})
api[16].list.push({
    order: '1',
    desc: '根据关键词搜索大学，关键词可以为空',
});
api.push({
    alias: 'UniversityMajorController',
    order: '18',
    desc: '大学专业相关接口',
    link: '大学专业相关接口',
    list: []
})
api[17].list.push({
    order: '1',
    desc: '获取所有专业(树形结构)',
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