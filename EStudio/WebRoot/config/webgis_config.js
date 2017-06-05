var WebGISConfig = {
    map : {
        lonlat : true
    },
    contain : {
        status : {
            layerTree : [true, "一张图"],
            spatialAnalyse : [true, "空间分析"],
            searchResult : [true, "查询结果"],
            plotting : [true, "地图标绘"],
            favorite : [true, "收藏夹"],
            share : [true, "分享地图"],
            project : [true, "专题应用"],
            statistic : [false, "统计报表"]
        },
        sort : ["一张图", "专题应用", "空间分析", "查询结果", "地图标绘", "收藏夹", "统计报表"]
        // ,exts : ["视频监控"]
    },
    layerTree : {
        detailList : true,
        overlayLayer : true,
        advanceSearch : true,
        specialView : true,
        abstractContent : true,
        export2excel : true,
        export2shape : true
    },
    toolbar : {
        zoomin : false,
        zoomout : false,
        measure : true,
        identify : true,
        gps : true,
        legend : true,
        compare : true,
        favorite : true,
        share : true,
        print : true,
        clear : true,
        searchRange : false,
        searchEnabled : true,
        navigator : true
    },
    search : {
        export2excel : true,
        export2shape : true
    },
    spatial : {
        search : true, // 控件查询
        buf : true, // 缓冲区分析
        district : true, // 行政区域分析
        statistic : true, // 统计分析
        network : true,
        gongjiaofg : false,//公交覆盖率
        resultLibrary:true,
        lwfgl:true,
        libraryList:"高速公路,公路,岸线分析结果",
    // 路径分析
        resultLibrary : true,
    },
    plotting : {
        polyline : false,
        polygon : false
    },
    // 专题随机颜色
    randomColors : [0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0xFF00FF, 0x00FFFF, 0x800000, 0x008000, 0x000080, 0x808000, 0x800080, 0x008080, 0xC0C0C0, 0x808080, 0x9999FF, 0x993366, 0xFFFFCC, 0xCCFFFF, 0x660066, 0xFF8080, 0x0066CC, 0xCCCCFF, 0x000080, 0xFF00FF, 0xFFFF00, 0x00FFFF, 0x800080, 0x800000,
            0x008080, 0x0000FF, 0x00CCFF, 0xCCFFFF, 0xCCFFCC, 0xFFFF99, 0x99CCFF, 0xFF99CC, 0xCC99FF, 0xFFCC99, 0x3366FF, 0x33CCCC, 0x99CC00, 0xFFCC00, 0xFF9900, 0xFF6600, 0x666699, 0x969696, 0x003366, 0x339966, 0x003300, 0x333300, 0x993300, 0x993366, 0x333399, 0x333333],
    // 渐进色
    stepColors : [0x2F0000, 0x4D0000, 0x600000, 0x750000, 0x930000, 0xAE0000, 0xCE0000, 0xEA0000, 0xFF0000, 0xFF2D2D, 0xFF5151, 0xff7575, 0xFF9797, 0xFFB5B5, 0xFFD2D2, 0xFFECEC, 0x000000, 0x272727, 0x3C3C3C, 0x4F4F4F, 0x5B5B5B, 0x6C6C6C, 0x7B7B7B, 0x8E8E8E, 0x9D9D9D, 0xADADAD, 0xBEBEBE, 0xd0d0d0,
            0xE0E0E0, 0xF0F0F0, 0xFCFCFC, 0xFFFFFF]

};
