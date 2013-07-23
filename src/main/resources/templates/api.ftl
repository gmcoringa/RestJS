JSAPI.formatQueryParams = function(params) {
	var buffer = "?";
	
	for(var k in params) {
		var v = params[k];
    	// Arrays, include items multiple times
		if(v instanceof Array === true){
	    	for(var i in v){
		        buffer += k + "=" + encodeURIComponent(v[i]) + "&";
	    	}
	    } else if(v || (!v && typeof(v) === 'boolean')) {
	        buffer += k + "=" + encodeURIComponent(v) + "&";
	    } 
	}
	
	return buffer;
};

/**
 * Configuration:
 * 		type: request type (GET, POST, etc)
 * 		async: true | false
 * 		url: url to send
 * 		dataType: data type expected from server
 * 		data: data to send
 * 		contentType: data type to send
 * 		object: object to be used as context on success or error functions
 * 		success: success function to be call on success and when async is false
 * 		error: error function to be call on request errors
 * 		params: parameters to be passed on error or success functions
 * 		queryParams: query parameters array
 */

JSAPI.restRequest = function(config) {
    var ret = null;
	
	if(config.queryParams) {
		url += this.formatQueryParams(config.queryParams);
	}
	
    jQuery.ajax({
        type : config.type,
        async : config.async,
        url : config.url ,
        dataType : config.dataType,
        data: config.data || '',
        contentType: config.contentType || 'application/x-www-form-urlencoded',
        success : function(response) {
        	if (!config.async) {
                ret = response;
            } else {
            	config.success.call(config.object, response, config.params);
            }
        },
        error : function(XMLHttpRequest, textStatus, errorThrown) {
            errorThrown = eval('(' + XMLHttpRequest.responseText + ')');
            if (config.errorCallback) {
            	config.errorCallback.call(config.oObj, XMLHttpRequest, textStatus, errorThrown, config.params);
            } else {
                throw errorThrown;
            }
        }
    });

    return ret;
};
