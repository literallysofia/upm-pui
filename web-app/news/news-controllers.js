/*************************************************************************************************
/////////////////////////////////////// NEWS CONTROLLERS /////////////////////////////////////////
*************************************************************************************************/

app.controller("NewsController", function ($scope, $rootScope, $window, $location, NewsListService) {
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

    $scope.getArticles();
});

app.controller("loginController", function ($scope, $rootScope, $http, $location, LoginService) {

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

app.controller("newsCreationCtrl", function ($scope, NewsDetailsService) {

    $scope.addNews = function (article) {
        article.img
        NewsDetailsService.save(article,
            function (data) {
                console.log(data);
            },
            function (error) {
                console.log("There was an error loading the news.");
                console.log(error);
                $window.alert("There was an error loading the news: " + error.statusText);
            });
    };

});