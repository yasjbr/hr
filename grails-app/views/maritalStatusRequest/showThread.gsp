<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'MaritalStatusRequest List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'maritalStatusRequest', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>

<g:render template="showThread" model="[maritalStatusRequestList:maritalStatusRequestList]" />

%{--the bellow rows to add space btw show widget--}%
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>

<el:row/>
<div class="clearfix form-actions text-center">
    <el:formButton functionName="back" goToPreviousLink="true"/>
</div>

</body>
</html>