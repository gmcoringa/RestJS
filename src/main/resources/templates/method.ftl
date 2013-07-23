<#-- Path function for method -->
<#--The path URL: "/url/{param}/other" becomes: "/url/"+encodeURIComponent(param)+"/other" -->
function ${nameSpace}.${name}.prototype.${name}_PATH = function(<#t>
<#list pathParams as pathParam><#t>
	<#assign find = "{" + pathParam + "}"><#t>
	<#assign replacement = "\"+encodeURIComponent(" + pathParam + ")+\""><#t>
	<#assign path = path?replace(find, replacement)><#t>
	${pathParam}<#t>
	<#if pathParam_has_next><#t>
		,<#t>
	</#if><#t>
</#list><#t>){
	return this.basePath() + "${path}";};

<#-- Method function --><#t>
function ${nameSpace}.${name}.prototype.${name} = function(async,<#t>
<#list paramNames as param><#t>
	${param},<#t>
</#list> object, success, error, params){
	var url = this.basePath() + "${path}";
	<#-- Query parameter format function --><#t>
	<#if queryParams?has_content><#t>
	url += JSAPI.formatQueryParams(<#t>
		<#list queryParams as qp><#t>
			${qp}<#t>
			<#if qp_has_next><#t>
				,<#t>
			</#if><#t>
		</#list>);
	</#if><#t>
	
	<#-- Form parameter --><#t>
	<#if formParams?has_content><#t>
	var formParams = {};
	<#list formParams as formParam><#t>
	formParams["${formParam}"] = "${formParam}";
	</#list><#t>
	var data = JSON.stringify(formParams);
	</#if><#t>
	<#-- Non annotated parameter --><#t>
	<#if dataContent?has_content><#t>
	var data = JSON.stringify(${dataContent});
	</#if><#t>
	var config = {
		"async" : async,
		"url" : url,
		"dataType" : ${dataType},
		"data" : data || null,
		"contentType" : ${contentType},
		"object" : object || null,
		"params" : params || null,
		"success" : success || null,
		"error" : error || null
	};
	
	JSAPI.restRequest(config);
};
