<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'jobTitle.entity', default: 'JobTitle List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'JobTitle List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'jobTitle',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${jobTitle?.descriptionInfo?.localName}" type="DescriptionInfo" label="${message(code:'jobTitle.descriptionInfo.localName.label',default:'localName')}" />
    <lay:showElement value="${jobTitle?.descriptionInfo?.latinName}" type="DescriptionInfo" label="${message(code:'jobTitle.descriptionInfo.latinName.label',default:'latinName')}" />
    <lay:showElement value="${jobTitle?.descriptionInfo?.hebrewName}" type="DescriptionInfo" label="${message(code:'jobTitle.descriptionInfo.hebrewName.label',default:'hebrewName')}" />
    <lay:showElement value="${jobTitle?.jobCategory?.descriptionInfo?.localName}" type="JobCategory" label="${message(code:'jobTitle.jobCategory.label',default:'jobCategory')}" />
    <lay:showElement value="${jobTitle?.transientData?.educationDegreeName}" type="Set" label="${message(code:'jobTitle.educationDegrees.label',default:'joinedJobTitleEducationDegrees')}" />
    <lay:showElement value="${jobTitle?.allowToRepeetInUnit}" type="Boolean" label="${message(code:'jobTitle.allowToRepeetInUnit.label',default:'allowToRepeetInUnit')}" />
    <lay:showElement value="${jobTitle?.joinedJobTitleOperationalTasks?.operationalTask?.descriptionInfo?.localName}" type="Set" label="${message(code:'jobTitle.operationalTask.label',default:'joinedJobTitleOperationalTasks')}" />
    <lay:showElement value="${jobTitle?.joinedJobTitleMilitaryRanks?.militaryRank?.descriptionInfo?.localName}" type="Set" label="${message(code:'jobTitle.militaryRank.label',default:'joinedJobTitleMilitaryRanks')}" />
    <lay:showElement value="${jobTitle?.joinedJobTitleJobRequirements?.jobRequirement?.descriptionInfo?.localName}" type="Set" label="${message(code:'jobTitle.JobRequirement.label',default:'joinedJobTitleJobRequirements')}" />
    <lay:showElement value="${jobTitle?.universalCode}" type="String" label="${message(code:'jobTitle.universalCode.label',default:'universalCode')}" />
    <lay:showElement value="${jobTitle?.note}" type="String" label="${message(code:'jobTitle.note.label',default:'note')}" />
</lay:showWidget>
<el:row />
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'jobTitle',action:'edit',params: [encodedId:"${jobTitle?.encodedId}"] )}'"/>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>
</body>
</html>