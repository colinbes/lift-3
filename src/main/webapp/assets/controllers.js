app.controller('SimpleRTCtrl',['$scope', function($scope){	
	console.log("Instantiating SimpleRTCtrl")
	$scope.lrtResModel="Click and I will do a server roundtrip";
	$scope.lrtResModel1="Click me";
	
	$scope.doSimpleLiftRT = function() {
	    var promise = myRTFunctions.doSimpleRT(); // call to lift function
	    return promise.then(function(data) {
	      $scope.$apply(function() {
	        $scope.lrtResModel = data;
	      })
	      return data;
	    });			
	};
	
	$scope.doResetRT = function() {
		return $scope.lrtResModel="Click again and I will do another roundtrip";
	};
	
	$scope.doSomethingRT = function() {
	    var promise = myRTFunctions.doSomething(); // call to lift function
	    return promise.then(function(data) {
	      $scope.$apply(function() {
	        $scope.lrtResModel1 = data.name;
	      })
	      return data;
	    });			
	};
	
	$scope.doResetSomething = function() {
		return $scope.lrtResModel1="Click again and I will do another roundtrip";
	};	
}]);

app.controller('ModelRTCtrl',['$scope', function($scope){
	console.log("Instantiating ModelRTCtrl")
	$scope.lrtResModel1="Click and I will do a server roundtrip";
	$scope.lrtResModel2="Click RT2 to run 2nd round trip";
	
	$scope.doRT1 = function() {
	    var promise = modelRTFunctions.page2RT1(); // call to lift function
	    return promise.then(function(data) {
	      $scope.$apply(function() {
	        $scope.lrtResModel1 = data;
	      })
	      return data;
	    });			
	};
	
	$scope.doReset1 = function() {
		return $scope.lrtResModel1="Click again and I will do another roundtrip";
	};
	
	$scope.doRT2 = function() {
	    var promise = modelRTFunctions.page2RT2(); // call to lift function
	    return promise.then(function(data) {
	      $scope.$apply(function() {
	        $scope.lrtResModel2 = data.name;
	      })
	      return data;
	    });			
	};
	
	$scope.doReset2 = function() {
		return $scope.lrtResModel2="Click again and I will do another roundtrip";
	};	
}]);