(function() {
    'use strict';

    angular
        .module('credmgrApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('jhi-configuration', {
            parent: 'admin',
            url: '/configuration',
            data: {
                authorities: ['OP_ADMIN'],
                pageTitle: 'configuration.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/configuration/configuration.html',
                    controller: 'JhiConfigurationController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('configuration');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
