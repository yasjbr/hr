<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'joinedFirmOperationDocument.entity', default: 'JoinedFirmOperationDocument List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'JoinedFirmOperationDocument List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'joinedFirmOperationDocument', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${joinedFirmOperationDocument?.operation}" type="enum"
                     label="${message(code: 'joinedFirmOperationDocument.operation.label', default: 'operation')}"
                     messagePrefix="EnumOperation"/>
</lay:showWidget>
<el:row/>
<g:if test="${joinedFirmOperationDocument?.transientData}">

    <lay:showWidget size="12" title="${message(code: 'joinedFirmOperationDocument.firmDocument.label')}">

    %{--<table id="detailsTable" class="pcpTable table  table-hover" border="border">--}%
    %{--<thead>--}%
    %{--<th class="center pcpHead">${message(code: 'joinedFirmOperationDocument.document.label')}</th>--}%
    %{--<th class="center pcpHead">${message(code: 'joinedFirmOperationDocument.isMandatory.label')}</th>--}%
    %{--</thead>--}%
    %{--<g:each in="${joinedFirmOperationDocument?.transientData?.firmDocumentOperation}" var="operation" status="index">--}%
    %{--<tr>--}%
    %{--<td>${operation?.firmDocument?.descriptionInfo?.localName}</td>--}%
    %{--<td>${operation?.isMandatoryTranslated}</td>--}%
    %{--</tr>--}%
    %{--</g:each>--}%
    %{--</table>--}%


        <table width="98%" frame="border" id="detailsTable"
               style="border-color: #336199;margin-right: 14px; display:block; width:98%; ">
            <thead style=" width:100%; display: table;">
            <th width="70%"
                style="color: #336199;background-color: #edf3f4; padding-right: 6px;height: 30px;">${message(code: 'joinedFirmOperationDocument.document.label')}<p
                    style="color:red;width: 1px;height: 1px;margin-top: -20px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*</p>
            </th>

            <th width="10%"
                style="color: #336199;background-color: #edf3f4; padding-right: 6px;">${message(code: 'joinedFirmOperationDocument.isMandatory.label')}<p
                    style="color:red;width: 1px;height: 1px;margin-top: -20px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*</p>
            </th>
            </thead>
            <tbody class="firmDocuments" style="overflow: auto;display: -moz-stack;height: 100px;width: 100%;">

            <g:each in="${joinedFirmOperationDocument?.transientData?.firmDocumentOperation}" var="operation"
                    status="index">
                <tr width='100%' id="row-${index + 1}" style="width: 100% ;border-bottom:1pt dotted #dcebf7;">
                    <td width="70%"
                        style="padding-right: 6px;">${operation?.firmDocument?.descriptionInfo?.localName}</td>
                    <td width="10%" style="padding-right: 6px;">${operation?.isMandatoryTranslated}</td>
                </tr>
            </g:each>
            </tbody>
        </table>
        <el:row/>
    </lay:showWidget>
</g:if>
<el:row/>
<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'joinedFirmOperationDocument', action: 'edit', params: ["transientData.operation": joinedFirmOperationDocument?.operation])}'"/>


    <btn:backButton goToPreviousLink="true"/>

</div>
</body>
</html>