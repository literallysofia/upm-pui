var app = angular.module("newssapp", ["ngRoute", "ngResource", "ngSanitize", "ngtweet", "textAngular"]);

// TODO: Code of the routeProvider

// TODO: Replace XXXXXXXXAPIKEY with the anonymous APIKEY of your group 
// When the user is logged in, the apikey sent to the server must be updated to the
// apikey received from the server. This update must be done in the corresponding controller
// $http.defaults.headers.common['Authorization'] = loginres.Authorization + ' apikey=' + loginres.apikey;

app.run(['$http', function ($http) {
	$http.defaults.headers.common['Authorization'] = 'PUIRESTAUTH apikey=DEV_TEAM_48392';
}]);

app.config(function ($routeProvider) {
	$routeProvider
		.when("/", {
			templateUrl: "news/news-list.html",
			controller: "NewsController"
		})
		.when("/login", {
			templateUrl: "news/news-login.html",
			controller: "LoginController"
		})
		.when('/create-article', {
			templateUrl: 'news/news-creation.html',
			controller: 'CreateArticleController'
		})
		.when('/article/:id', {
			templateUrl: 'news/news-show.html', 
			controller: 'ShowArticleController'
		})
		.when('/editArticle/:id', {
			templateUrl: 'news/news-edition.html', 
			controller: 'EditArticleController'
		})
		.otherwise({
			templateUrl: 'news/news-list.html',
			controller: 'NewsController'
		});
});

/**************************************************************************/
// Code to load the image files 
app.controller('UploadController', function ($scope, fileReader) {
	$scope.imageSrc = "";

	$scope.$on("fileProgress", function (e, progress) {
		$scope.progress = progress.loaded / progress.total;
	});
});

app.directive("ngFileSelect", function (fileReader, $timeout) {
	return {
		scope: {
			ngModel: '='
		},
		link: function ($scope, el) {
			function getFile(file) {
				fileReader.readAsDataUrl(file, $scope)
					.then(function (result) {
						$timeout(function () {
							$scope.ngModel.image_media_type = result.substring(5, result.indexOf(";base64"));
							$scope.ngModel.image_data = result.substring(result.indexOf(";base64,") + 8, result.length);

							//$scope.ngModel. = result;
						});
					});
			}

			el.bind("change", function (e) {
				var file = (e.srcElement || e.target).files[0];
				getFile(file);
			});
		}
	};
});

app.factory("fileReader", function ($q, $log) {
	var onLoad = function (reader, deferred, scope) {
		return function () {
			scope.$apply(function () {
				deferred.resolve(reader.result);
			});
		};
	};

	var onError = function (reader, deferred, scope) {
		return function () {
			scope.$apply(function () {
				deferred.reject(reader.result);
			});
		};
	};

	var onProgress = function (reader, scope) {
		return function (event) {
			scope.$broadcast("fileProgress", {
				total: event.total,
				loaded: event.loaded
			});
		};
	};

	var getReader = function (deferred, scope) {
		var reader = new FileReader();
		reader.onload = onLoad(reader, deferred, scope);
		reader.onerror = onError(reader, deferred, scope);
		reader.onprogress = onProgress(reader, scope);
		return reader;
	};

	var readAsDataURL = function (file, scope) {
		var deferred = $q.defer();

		var reader = getReader(deferred, scope);
		reader.readAsDataURL(file);

		return deferred.promise;
	};

	return {
		readAsDataUrl: readAsDataURL
	};
});