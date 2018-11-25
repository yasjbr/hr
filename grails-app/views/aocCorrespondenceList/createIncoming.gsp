<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'EnumCorrespondenceDirection.INCOMING', default: 'Incoming')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'Create Incoming')}" />
    <title>${title}</title>
    <g:render template="script"/>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        %{--<btn:buttonGroup>--}%
            %{--<btn:listButton onClick="window.location.href='${createLink(controller:'aocCorrespondenceList',action:'list')}'"/>--}%
        %{--</btn:buttonGroup>--}%
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="aocCorrespondenceListForm" callLoadingFunction="performPostActionWithEncodedId" controller="aocCorrespondenceList" action="save">
                <g:render template="/aocCorrespondenceList/formAoc" model="[aocCorrespondenceList:aocCorrespondenceList]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
