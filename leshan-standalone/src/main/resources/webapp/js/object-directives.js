/*!
 * Copyright (c) 2013-2014, Sierra Wireless
 * Released under the BSD license
 * https://raw.githubusercontent.com/jvermillard/leshan/master/LICENSE
 */

angular.module('objectDirectives', [])

.directive('object', function ($compile, $routeParams, $http, dialog,$filter,$modal,lwResources) {
    return {
        restrict: "E",
        replace: true,
        scope: {
            object: '=',
            parent: '='
        },
        templateUrl: "partials/object.html",
        link: function (scope, element, attrs) {
            var parentPath = "";
            if(scope.parent) {
                parentPath = scope.parent.path;
            }
            scope.status = {};
            scope.status.open = true;
            
            scope.object.path = parentPath + "/" + scope.object.id;
            scope.object.create  =  {tooltip : "Create <br/>"   + scope.object.path};
            
            scope.create = function () {
                var modalInstance = $modal.open({
                  templateUrl: 'partials/modal-instance.html',
                  controller: 'modalInstanceController',
                  resolve: {
                    object: function(){ return scope.object},
                    instanceId: function(){ return null},
                  }
                });
            
                modalInstance.result.then(function (instance) {
                    // Build payload
                    var payload = {};
                    payload["id"] = instance.id;
                    payload["resources"] = []

                    for(i in instance.resources){
                        var resource = instance.resources[i];
                        if (resource.value != undefined){
                            payload.resources.push({
                                id:resource.id,
                                value:lwResources.getTypedValue(resource.value, resource.def.type)
                            })
                        } 
                    }
                    // Send request
                    var instancepath  = scope.object.path + "/" + instance.id
                    $http({method: 'POST', url: "api/clients/" + $routeParams.clientId + instancepath, data: payload, headers:{'Content-Type': 'application/json'}})
                    .success(function(data, status, headers, config) {
                        create = scope.object.create;
                        create.date = new Date();
                        var formattedDate = $filter('date')(create.date, 'HH:mm:ss.sss');
                        create.status = data.status;
                        create.tooltip = formattedDate + "<br/>" + create.status;
                        
                        if (data.status == "CREATED") {
                            for (var i in payload.resources) {
                                var tlvresource = payload.resources[i];
                                var newinstance = lwResources.addInstance(scope.object, instance.id, null)
                                resource = lwResources.addResource(scope.object, newinstance, tlvresource.id, null)
                                resource.value = tlvresource.value;
                                resource.valuesupposed = true;
                                resource.tooltip = formattedDate;
                            }
                        }
                    }).error(function(data, status, headers, config) {
                        errormessage = "Unable to create instance " + instancepath + " for "+ $routeParams.clientId + " : " + status +" "+ data
                        dialog.open(errormessage);
                        console.error(errormessage)
                    });;
                });
            };
        }
    }
});
