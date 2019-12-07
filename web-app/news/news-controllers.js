/*************************************************************************************************
/////////////////////////////////////// NEWS CONTROLLERS /////////////////////////////////////////
*************************************************************************************************/

app.controller("newsListController", function ($scope, NewsListService, fileReader) {

    $scope.getArticles = function () {
        NewsListService.query({}, function (data) {
                console.log("News obtained successfully!");
                console.log(data);
                $scope.articles = data;
            },
            function (error) {
                console.log("There was an error loading the news.");
                console.log(error);
                $window.alert("There was an error loading the news: " + error.statusText);
            })
    };

    $scope.getArticles();
});