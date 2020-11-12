/*!
 * u main JS.
 */
"use strict";
//# sourceURL=u.js

// DOM 加载完再执行
$(function () {
    const blogUrl = window.document.location.pathname;
    const projectName = blogUrl.substring(0, blogUrl.substr(1).indexOf('/') + 1);
    const username = blogUrl.substring(blogUrl.indexOf('/u/') + 3, blogUrl.lastIndexOf('/blogs'))

    let _pageSize; // 存储用于搜索
    let catalogId;

    // 获取指定用户的博客列表
    function getBlogsByName(pageIndex, pageSize) {
        $.ajax({
            url: "/blog/u/" + username + "/blogs",
            contentType: 'application/json',
            data: {
                "async": true,
                "pageIndex": pageIndex,
                "pageSize": pageSize,
                "keyword": $("#keyword").val()
            },
            success: function (data) {
                $("#mainContainer").html(data);
            },
            error: function () {
                toastr.error("error!");
            }
        });
    }

    // 分页
    $.tbpage("#mainContainer", function (pageIndex, pageSize) {
        getBlogsByName(pageIndex, pageSize);
        _pageSize = pageSize;
    });

    // 关键字搜索
    $("#searchBlogs").click(function () {
        getBlogsByName(0, _pageSize);
    });

    // 最新、最热切换事件
    $(".nav-item .nav-link").click(function () {

        var url = $(this).attr("url");

        // 先移除其他的点击样式，再添加当前的点击样式
        $(".nav-item .nav-link").removeClass("active");
        $(this).addClass("active");

        // 加载其他模块的页面到右侧工作区
        $.ajax({
            url: url + '&async=true',
            success: function (data) {
                $("#mainContainer").html(data);
            },
            error: function () {
                toastr.error("error!");
            }
        })

        // 清空搜索框内容
        $("#keyword").val('');
    });

    function getCatalogs(username) {
        $.ajax({
            url: projectName + '/catalogs',
            type: 'GET',
            data: {"username": username},
            success: function (data) {
                $("#catalogMain").html(data);
            },
            error: function () {
                toastr.error("error!");
            }
        });
    }


    // 获取编辑分类的页面
    $(".blog-content-container").on("click", ".blog-add-catalog", function () {
        $.ajax({
            url: projectName + '/catalogs/edit',
            type: 'GET',
            success: function (data) {
                $("#catalogFormContainer").html(data);
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 获取编辑某个分类的页面
    $(".blog-content-container").on("click", ".blog-edit-catalog", function () {

        $.ajax({
            url: projectName + '/catalogs/edit/' + $(this).attr('catalogId'),
            type: 'GET',
            success: function (data) {
                $("#catalogFormContainer").html(data);
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 提交分类
    $("#submitEditCatalog").click(function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: projectName + '/catalogs',
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "username": username,
                "catalog": {"id": $('#catalogId').val(), "name": $('#catalogName').val()}
            }),
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    toastr.info(data.message);
                    // 成功后，刷新列表
                    getCatalogs(username);
                } else {
                    toastr.error(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 删除分类
    $(".blog-content-container").on("click", ".blog-delete-catalog", function () {
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: projectName + '/catalogs/' + $(this).attr('catalogid') + '?username=' + username,
            type: 'DELETE',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    toastr.info(data.message);
                    // 成功后，刷新列表
                    getCatalogs(username);
                } else {
                    toastr.error(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 根据分类查询
    $(".blog-content-container").on("click", ".blog-query-by-catalog", function () {
        catalogId = $(this).attr('catalogId');
        getBlogsByName(0, _pageSize);
    });

});