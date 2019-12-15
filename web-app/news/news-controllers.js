/*************************************************************************************************
/////////////////////////////////////// NEWS CONTROLLERS /////////////////////////////////////////
*************************************************************************************************/

app.controller("NewsController", function ($scope, $rootScope, $window, $location, NewsListService, NewsDetailsService) {

    $rootScope.currentPath = $location.path();
    $scope.articleToDelete = {};
    $scope.showModal = false;
    $scope.showAlert = false;

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

    $scope.toggleModal = function (article) {
        $scope.articleToDelete = article;
        $scope.showModal = !$scope.showModal;
    };

    $scope.deleteArticle = function () {
        console.log("Deleting article...");
        NewsDetailsService.delete({
                id: $scope.articleToDelete.id
            },
            function (data) {
                console.log(data);
                console.log("Article deleted!");
                $scope.showAlert = true;
                $scope.getArticles();
            },
            function (error) {
                console.log("There was an error when trying to delete the article.");
                console.log(error);
                $window.alert("There was an error when trying to delete the article: " + error.statusText);
            })
    };

});

app.controller("LoginController", function ($scope, $rootScope, $http, $location, LoginService) {

    $rootScope.currentPath = $location.path();

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

app.controller("ShowArticleController", function ($scope, $routeParams, NewsDetailsService) {

    $rootScope.currentPath = $location.path();

    $scope.getArticle = function () {
        NewsDetailsService.get({
                id: parseInt($routeParams.id)
            },
            function (data) {
                console.log(data);
                $scope.article = data;
            },
            function (error) {
                console.log("There was an error loading the article.");
                console.log(error);
            });
    }

    $scope.getArticle();
});

app.controller("EditArticleController", function ($scope, $rootScope, $routeParams, $location, NewsDetailsService) {

    $rootScope.currentPath = $location.path();
    console.log($rootScope.currentPath);

    $scope.getArticle = function () {
        NewsDetailsService.get({
                id: parseInt($routeParams.id)
            },
            function (data) {
                console.log(data);
                $scope.article = data;
            },
            function (error) {
                console.log("There was an error loading the article.");
                console.log(error);
            });
    }

    $scope.getArticle();

    $scope.createArticle = function () {
        NewsDetailsService.save($scope.article,
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

app.controller("CreateArticleController", function ($scope, $rootScope, $window, $location, NewsDetailsService) {

    $rootScope.currentPath = $location.path();
    $scope.article = {};
    new FroalaEditor('textarea#froala-editor')

    $scope.createArticle = function () {
        NewsDetailsService.save($scope.article,
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

app.directive('modal', function () {
    return {
        template: '<div class="modal fade">' +
            '<div class="modal-dialog modal-dialog-centered" role="document">' +
            '<div class="modal-content">' +
            '<div class="modal-header">' +
            '<h5 class="modal-title" id="modalCenterTitle">Are you sure?</h5>' +
            '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
            '</div>' +
            '<div class="modal-body">' +
            'Do you really want to delete the article "{{articleToDelete.title}}"? <br> This process cannot be undone.' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-dark" data-dismiss="modal">Cancel</button>' +
            '<button type="button" class="btn btn-danger" ng-click="delete()">Delete</button>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>',
        restrict: 'E',
        transclude: true,
        replace: true,
        scope: true,
        link: function postLink(scope, element, attrs) {
            scope.$watch(attrs.visible, function (value) {
                if (value == true)
                    $(element).modal('show');
                else
                    $(element).modal('hide');
            });

            scope.delete = function () {
                scope.deleteArticle();
                $(element).modal('hide');
            };

            $(element).on('shown.bs.modal', function () {
                scope.$apply(function () {
                    scope.$parent[attrs.visible] = true;
                });
            });

            $(element).on('hidden.bs.modal', function () {
                scope.$apply(function () {
                    scope.$parent[attrs.visible] = false;
                });
            });
        }
    };
});