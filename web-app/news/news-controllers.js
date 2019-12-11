/*************************************************************************************************
/////////////////////////////////////// NEWS CONTROLLERS /////////////////////////////////////////
*************************************************************************************************/

app.controller("newsController", function ($scope, NewsListService) {

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


app.controller("newsCreationCtrl", function ($scope, NewsDetailsService) {

    $scope.addNews = function(article){
        NewsDetailsService.save(article, 
        function(data){
            console.log(data);
        },
        function (error) {
            console.log("There was an error loading the news.");
            console.log(error);
            $window.alert("There was an error loading the news: " + error.statusText);
        });

        
    };

});
