/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	config.removeDialogTabs = 'image:advanced;link:advanced';
	
    config.filebrowserBrowseUrl = 'ckfinder/ckfinder.html';
    config.filebrowserImageBrowseUrl = '../htmlEditor/ckfinder/ckfinder.html?Type=Images';
    config.filebrowserFlashBrowseUrl = '../htmlEditor/ckfinder/ckfinder.html?Type=Flash';
    config.filebrowserUploadUrl = '../htmlEditor/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Files';
    config.filebrowserImageUploadUrl = '../htmlEditor/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Images';
    config.filebrowserFlashUploadUrl = '../htmlEditor/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Flash';

    config.uiColor = '#EBF4FF';
    config.toolbar = [
                  	{ name: 'document', items: [ 'Preview', 'Maximize' ] },
                  	{ name: 'clipboard', items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },
                  	{ name: 'editing', items: [ 'Find', 'Replace', '-', 'SelectAll' ] },
                  	{ name: 'styles', items: [ 'Font', 'FontSize' ] },
                  	{ name: 'colors', items: [ 'TextColor', 'BGColor' ] },
                  	'/',
                  	{ name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline', 'Subscript', 'Superscript', '-', 'RemoveFormat' ] },
                  	{ name: 'paragraph', items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ] },
                  	{ name: 'links', items: [ 'Link', 'Unlink' ] },
                  	{ name: 'insert', items: [ 'Image', 'Flash', 'Table','flvPlayer' ] }
                  ];
    config.extraPlugins = 'flvPlayer';
    
    //默认的字体名 plugins/font/plugin.js
    config.font_defaultLabel = '微软雅黑';

    //字体编辑时的字符集 可以添加常用的中文字符：宋体、楷体、黑体等plugins/font/plugin.js
    config.font_names = '微软雅黑;宋体;Arial;Times NewRoman;Verdana';
};
