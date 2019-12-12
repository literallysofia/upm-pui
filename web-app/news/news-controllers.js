/*************************************************************************************************
/////////////////////////////////////// NEWS CONTROLLERS /////////////////////////////////////////
*************************************************************************************************/

app.controller("NewsController", function ($scope, $rootScope, $window, $location, NewsListService, NewsDetailsService) {
    $rootScope.currentPath = $location.path();

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

    $scope.deleteArticle = function (article) {

        console.log("Delete article: " + article.title);

        console.log(article);

        if (confirm('The article: "' + article.title + '" is going to be removed. Are you sure?')) {

            NewsDetailsService.delete(article,

                function (data) {
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

app.controller("LoginController", function ($scope, $rootScope, $http, $location, LoginService) {

    $rootScope.currentPath = $location.path();
    console.log($rootScope.currentPath);

    $scope.login = function () {
        LoginService.login({
                passwd: $scope.login.password,
                username: $scope.login.username
            }, function (loginres) {
                $http.defaults.headers.common['Authorization'] = loginres.Authorization + ' apikey=' + loginres.apikey;
                $scope.login = {};
                $scope.loginForm.$setPristine();
                $rootScope.loggedUser = loginres.username;
                console.log("Successful login!");
                console.log(loginres);
                $location.path('/');
            },
            function (error) {
                console.log("There was an error with the login.");
                console.log(error);
                $scope.login.error = true;
            })
    };

});

app.controller("LogoutController", function ($scope, $rootScope, $http) {

    $scope.logout = function () {
        $http.defaults.headers.common['Authorization'] = 'PUIRESTAUTH apikey=DEV_TEAM_48392';
        $rootScope.loggedUser = null;
        console.log("Successful logout!");
    };

});

app.controller("newsDetailCtrl", function ($scope, $routeParams, NewsDetailsService) {

    $scope.getArticle = function () {
        NewsDetailsService.get($routeParams.id,
            function (data) {
                console.log(data);
                //$scope.article = data;
            },
            function (error) {
                console.log("There was an error loading the news.");
                console.log(error);
            });
    }

    $scope.article = $scope.getArticle();
});

app.controller("newsCreationCtrl", function ($scope, $window, $location, NewsDetailsService) {

    new FroalaEditor('textarea#froala-editor')

    $scope.addNews = function (article) {
        NewsDetailsService.save(article,
            function (data) {
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