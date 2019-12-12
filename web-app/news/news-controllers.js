/*************************************************************************************************
/////////////////////////////////////// NEWS CONTROLLERS /////////////////////////////////////////
*************************************************************************************************/

app.controller("newsController", function ($scope, $location, $window, NewsListService, NewsDetailsService) {

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

    $scope.deleteArticle = function(article){
        
        console.log("Delete article: " + article.title);

        console.log(article);
       
        if (confirm('The article: "' + article.title + '" is going to be removed. Are you sure?')) {
            
            NewsDetailsService.delete(article, 
               
                function(data){
                    console.log(data);
                   
                },
                function (error) {
                    console.log("There was an error deleting.");
                    console.log(error);
                    $window.alert("There was an error deleting the news: " + error.statusText);
                })
        }

        $location.path("/");
    };

    $scope.getArticles();
});


app.controller("newsCreationCtrl", function ($scope, $window, $location, NewsDetailsService) {

    $scope.addNews = function(article){
        article.img
        NewsDetailsService.save(article, 
        function(data){
            console.log(data);
        },
        function (error) {
            console.log("There was an error loading the news.");
            console.log(error);
            $window.alert("There was an error loading the news: " + error.statusText);
        });

        $location.path("/");
        
    };

});
