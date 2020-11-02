/*!
  * Bolg main JS.
 */
"use strict";

$(function () {
    const pathName = window.document.location.pathname;
    //获取带"/"的项目名，如：/proj
    const projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);

    let _pageSize;

    // 根据用户名、页面索引、页面大小获取用户列表
    function getUersByName(pageIndex, pageSize) {
        $.ajax({
            url: projectName + "/users",
            contentType: 'application/json',
            data: {
                "async": true,
                "pageIndex": pageIndex,
                "pageSize": pageSize,
                "name": $("#searchName").val()
            },
            success: function (data) {
                $("#mainContainer").html(data);
            },
            error: function () {
                toastr.error("error!");
            }
        });
    }

    // //分页
    // $.tbpage("#mainContainer", function (pageIndex, pageSize) {
    // 	getUersByName(pageIndex, pageSize);
    // 	_pageSize = pageSize;
    // });

    // 搜索
    $("#searchNameBtn").click(function () {
        getUersByName(0, _pageSize);
    });

    // 获取添加用户的界面
    $("#addUser").click(function () {
        $.ajax({
            url: projectName + "/users/add",
            success: function (data) {
                $("#userFormContainer").html(data);
            },
            error: function (data) {
                toastr.error("error!" + data);
            }
        });
    });

    // 获取编辑用户的界面
    $("#rightContainer").on("click", ".blog-edit-user", function () {
        $.ajax({
            url: projectName + "/users/edit/" + $(this).attr("userId"),
            success: function (data) {
                $("#userFormContainer").html(data);
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 提交变更，清空表单
    $("#submitEdit").click(function () {
        $.ajax({
            url: projectName + "/users",
            type: 'POST',
            data: $('#userForm').serialize(),
            success: function (data) {
                $('#userForm')[0].reset();

                if (data.success) {
                    // 从新刷新主界面
                    getUersByName(0, _pageSize);
                } else {
                    toastr.error(data.message);
                }

            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 删除用户
    $("#mainContainer").on("click", ".blog-delete-user", function () {
        // 获取 CSRF Token
        const csrfToken = $("meta[name='_csrf']").attr("content");
        const csrfHeader = $("meta[name='_csrf_header']").attr("content");

        const deleteUrl = projectName + "/users/" + $(this).attr("userId")

        $.ajax({
            url: deleteUrl,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 在请求头添加CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    getUersByName(0, _pageSize);    //删除成功刷新主界面
                } else {
                    console.log("del fail");
                }
            },
            error: function () {
                console.log("fun fail");
            }
        });
    });
});