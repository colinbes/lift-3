function getScope() { //just for ease of testing
	return angular.element('#controller').scope();
}

app.controller('SwitchCtrl',['$scope',function($scope) {
	console.log("instantiate SwitchCtrl")
	$scope.editChoice = "edit"
	$scope.category = {
		name : "cat1"
	}
	$scope.rename = {
		category : "tag1"
	}
}])

app.controller('SimpleRTCtrl',['$rootScope','$scope',function($rootScope, $scope) {
		$scope.lrtResModel = "Click and I will do a server roundtrip!";
		$scope.lrtResModel1 = "Click me";
		$scope.myClock = "--"
		console.log("instantiate SimpleRT")
		
		$scope.doSimpleLiftRT = function() {
			var promise = myRTFunctions.doSimpleRT(); // call
			// to lift function
			return promise.then(function(data) {
				$scope.$apply(function() {
					$scope.lrtResModel = data;
				})
				return data;
			});
		};	
		
		$scope.doResetRT = function() {
			return $scope.lrtResModel = "Click again and I will do another roundtrip";
		};
	
		$scope.doSomethingRT = function() {
			var promise = myRTFunctions.doSomething(); // call
	
			return promise.then(function(data) {
				$scope.$apply(function() {
					$scope.lrtResModel1 = data.name;
				})
				return data;
			});
		};
	
		$scope.doResetSomething = function() {
			return $scope.lrtResModel1 = "Click again and I will do another roundtrip";
		};
		$scope.myObj = "--"
		$scope.$on('emit-object', function(e, obj) {
				$scope.myObj = obj.name
	  });	

		function heartbeat() {
			myRTFunctions.heartbeat()
			setTimeout(heartbeat, 2000)
		}
		
		heartbeat()
		
		function doClock() {
			var promise = myRTFunctions.doClock(); // call
			
			return promise.then(function(data) {
				$scope.$apply(function() {
					$scope.myClock = data;
				})
				return data;
			});
		};
		
		doClock()
		
		$(document).on('new-message', function(event, data) {
			console.log(data)
			$scope.$apply(function() {
				$scope.myCometMessage = data				
			})
		});
		
	} ]);

app.controller('ModelRTCtrl', [ '$scope', function($scope) {
	$scope.lrtResModel1 = "Click and I will do a server roundtrip";
	$scope.lrtResModel2 = "Click RT2 to run 2nd round trip";

	$scope.doRT1 = function() {
		var promise = modelRTFunctions.page2RT1(); // call

		return promise.then(function(data) {
			$scope.$apply(function() {
				$scope.lrtResModel1 = data;
			})
			return data;
		});
	};

	$scope.doReset1 = function() {
		return $scope.lrtResModel1 = "Click again and I will do another roundtrip";
	};

	$scope.doRT2 = function() {
		var promise = modelRTFunctions.page2RT2(); // call
		// to
		// lift
		// function
		return promise.then(function(data) {
			$scope.$apply(function() {
				$scope.lrtResModel2 = data.name;
			})
			return data;
		});
	};

	$scope.doReset2 = function() {
		return $scope.lrtResModel2 = "Click again and I will do another roundtrip";
	};
} ]);

app.controller('AccordionDemoCtrl', [ '$scope', function($scope) {
	$scope.oneAtATime = true;

	$scope.groups = [ {
		title : 'Dynamic Group Header - 1',
		content : 'Dynamic Group Body - 1'
	}, {
		title : 'Dynamic Group Header - 2',
		content : 'Dynamic Group Body - 2'
	} ];

	$scope.items = [ 'Item 1', 'Item 2', 'Item 3' ];

	$scope.addItem = function() {
		var newItemNo = $scope.items.length + 1;
		$scope.items.push('Item ' + newItemNo);
	};

	$scope.status = {
		isCustomHeaderOpen : false,
		isFirstOpen : true,
		isFirstDisabled : false
	};
} ]);