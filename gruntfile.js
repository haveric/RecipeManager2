module.exports = function (grunt) {

    // Just in Time plugin loader (Only loads the plugins used for the task)
    require('jit-grunt')(grunt);

    // Time how long tasks take. Can help when optimizing build times
    require('time-grunt')(grunt);

    // Configurable paths
    var config = {
        templatesBase: '',

        // img locations
        img: 'img',

        // less locations
        less: 'less',
        outputCss: 'dist/css'
    };

    grunt.initConfig({
        // Project settings
        config: config,
        pkg: grunt.file.readJSON('package.json'), // the package file to use
        
        imagemin: {
            options: {
                optimizationLevel: 7
            },
            dynamic: {
                files: [{
                    expand: true,
                    cwd: '<%= config.img %>',
                    src: ['**/*.{png,jpg,gif}'],
                    dest: '<%= config.img %>'
                }]
            }
        },
        less : {
            development : {
                options : {
                    outputSourceFiles : true,
                    sourceMap : true,
                    compress : false,
                    cleancss : false
                },
                files : {
                    "<%= config.outputCss %>/main.css" : "<%= config.less %>/main.less"
                }
            },
            production : {
                options : {
                    outputSourceFiles : true,
                    sourceMap : false,
                    compress : false,
                    cleancss : false
                },
                files : {
                    "<%= config.outputCss %>/main.css" : "<%= config.less %>/main.less"
                }
            }
        },
        watch : {
            html: {
                files : [ '<%= config.templatesBase %>**.html' ],
                options: {
                    livereload: true
                }
            },
            css : {
                files : [ '<%= config.less %>/**/*.less' ],
                tasks : [ 'less:development' ],
                options: {
                    livereload: true
                }
            }
        }
    });

    //by default
    grunt.registerTask('default', ['less:development']);
    //by watch
    grunt.registerTask('server', ['default', 'watch']);

    //build
    grunt.registerTask('deploy', ['less:production', 'imagemin']);
    //build without imagemin
    grunt.registerTask('deploy-light', ['less:production']);

};
