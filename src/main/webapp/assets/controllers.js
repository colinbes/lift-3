function getScope() { //just for ease of testing
	return angular.element('#controller').scope();
}

var bcController = angular.module('bc.controllers', []);

bcController.controller('SimpleCtrl',['$rootScope','$scope', 'indexService', function($rootScope, $scope, indexService) {
	$scope.testdata = ""

	$scope.getDataFuture = function(tag) {
		$scope.testdata = "waiting"
		indexService.getDataFuture(tag).then(function(res){
			console.log("Got future data", res)
			$scope.testdata = res
		}, function(err){
			console.error("future error", err)
		})
	}
	$scope.getDataSync = function(tag) {
		$scope.testdata = "waiting"
		indexService.getData(tag).then(function(res){
			console.log("Got data", res)
			$scope.testdata = res
		}, function(err){
			console.error("error", err)
		})
	}
} ]);