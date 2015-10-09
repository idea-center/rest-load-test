var app = angular.module('ideaRestLoadTest', []);

app.controller('GetReportsController', ['$scope', '$http', function($scope, $http){
	$scope.testTitle = 'This is an Angular app';
	$scope.params = {isAngular: true, host: 'localhost', port: 8091, appName: 'IOL3', appKey: '872ttyu8d47a07c6330430lkq39500c5072bp822', reportThreads: 1, questionThreads: 1, reportCount: 10, surveyThreads: 1, surveyCount: 10};
	$scope.reports = {};
	$scope.reportModels = {};
	$scope.reportQuestionModels = {};
	$scope.surveysRead = {};
	$scope.surveysPosted = {};
	
	$scope.getReports = function(){
		console.log($scope.params);
		$scope.reports = {};
		$http.get(getReportsUrl, {params: $scope.params})
		.success(function(response){
			$scope.reports = response;
			console.log(response);
		})
		.error(function(error){
			$scope.result = error;
		});
	};
	
	$scope.getReportModels = function(){
		console.log($scope.params);
		$scope.reportModels = {};
		$http.get(getReportModelsUrl, {params: $scope.params})
		.success(function(response){
			$scope.reportModels = response;
			console.log(response);
		})
		.error(function(error){
			$scope.result = error;
		});
	};
	
	$scope.getReportQuestionModels = function(){
		console.log($scope.params);
		$scope.reportQuestionModels = {};
		$http.get(getReportQuestionModelsUrl, {params: $scope.params})
		.success(function(response){
			$scope.reportQuestionModels = response;
			console.log(response);
		})
		.error(function(error){
			$scope.result = error;
		});
	};
	
	$scope.getSurveys = function(){
		console.log($scope.params);
		$scope.surveysRead = {};
		$http.get(getSurveysUrl, {params: $scope.params})
		.success(function(response){
			$scope.surveysRead = response;
			console.log(response);
		})
		.error(function(error){
			$scope.result = error;
		});
	};
	
	$scope.postSurveys = function(){
		console.log($scope.params);
		$scope.surveysPosted = {};
		$http.get(postSurveysUrl, {params: $scope.params, method: 'POST'})
		.success(function(response){
			$scope.surveysPosted = response;
			console.log(response);
		})
		.error(function(error){
			$scope.result = error;
		});
	};
}]);