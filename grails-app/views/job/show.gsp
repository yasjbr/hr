<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'job.entity', default: 'Job List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'Job List')}"/>
    <title>${title}</title>
</head>

<body>


<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'job', action: 'list')}'"/>
    </div></div>
</div>

<el:row/>

<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${job?.code}" type="String" label="${message(code: 'job.code.label', default: 'code')}"/>
    <lay:showElement value="${job?.descriptionInfo?.localName}" type="string"
                     label="${message(code: 'job.descriptionInfo.localName.label', default: 'localName')}"/>
    <lay:showElement value="${job?.descriptionInfo?.latinName}" type="string"
                     label="${message(code: 'job.descriptionInfo.latinName.label', default: 'latinName')}"/>
    <lay:showElement value="${job?.descriptionInfo?.hebrewName}" type="string"
                     label="${message(code: 'job.descriptionInfo.hebrewName.label', default: 'hebrewName')}"/>
    <lay:showElement value="${job?.jobCategory?.descriptionInfo?.localName}" type="JobCategory"
                     label="${message(code: 'job.jobCategory.label', default: 'jobCategory')}"/>
    <lay:showElement value="${job?.transientData?.educationDegreeName}" type="Set"
                     label="${message(code: 'job.educationDegrees.label', default: 'joinedJobEducationDegrees')}"/>
    <lay:showElement
            value="${job?.joinedJobInspectionCategories?.inspectionCategory?.descriptionInfo?.localName?.sort()}"
            type="Set"
            label="${message(code: 'jobRequisition.inspectionCategories.label', default: 'joinedJobInspectionCategories')}"/>
    <lay:showElement value="${job?.joinedJobMilitaryRanks?.militaryRank?.descriptionInfo?.localName}" type="Set"
                     label="${message(code: 'job.militaryRank.label', default: 'joinedJobMilitaryRanks')}"/>
    <lay:showElement value="${job?.joinedJobOperationalTasks?.operationalTask?.descriptionInfo?.localName}" type="Set"
                     label="${message(code: 'job.operationalTask.label', default: 'joinedJobOperationalTasks')}"/>
    <lay:showElement value="${job?.universalCode}" type="String"
                     label="${message(code: 'job.universalCode.label', default: 'universalCode')}"/>
    <lay:showElement value="${job?.note}" type="String" label="${message(code: 'job.note.label', default: 'note')}"/>
</lay:showWidget>
<el:row/>


<g:if test="${job?.fromAge || job?.toAge || job?.fromWeight || job?.toWeight || job?.fromHeight || job?.toHeight}">

    <lay:showWidget size="12"
                    title="${message(code: 'jobRequisition.otherRequirements.label', default: 'otherRequirements List')}">

        <g:if test="${job?.fromAge || job?.toAge}">
            <div class="row" style="  margin-right: 0px; margin-left: 0px;">
                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">

                    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
                        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                            <lay:showElement value="${job?.fromAge}" type="Short"
                                             label="${message(code: 'job.fromAge.label', default: 'fromAge')}"/>
                        </div>

                        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                            <lay:showElement value="${job?.toAge}" type="Short"
                                             label="${message(code: 'job.toAge.label', default: 'toAge')}"/>
                        </div>
                    </div>

                </div>
            </div>

        </g:if>

        <g:if test="${job?.fromHeight || job?.toHeight}">
            <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
                        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                            <lay:showElement value="${job?.fromHeight}" type="Float"
                                             label="${message(code: 'job.fromHeight.label', default: 'fromHeight')}"/>
                        </div>

                        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                            <lay:showElement value="${job?.toHeight}" type="Float"
                                             label="${message(code: 'job.toHeight.label', default: 'toHeight')}"/>
                        </div>
                    </div>
                </div>
            </div>

        </g:if>
        <g:if test="${job?.fromWeight || job?.toWeight}">
            <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
                <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                    <div class="row" style=" border-top: 1px dotted #dcebf7; margin-right: 0px; margin-left: 0px;">
                        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                            <lay:showElement value="${job?.fromWeight}" type="Float"
                                             label="${message(code: 'job.fromWeight.label', default: 'fromWeight')}"/>
                        </div>

                        <div class="col-sm-6" style="padding-right: 0px; padding-left: 0px;">
                            <lay:showElement value="${job?.toWeight}" type="Float"
                                             label="${message(code: 'job.toWeight.label', default: 'toWeight')}"/>
                        </div>
                    </div>
                </div>
            </div>

        </g:if>
    </lay:showWidget>
</g:if>
<el:row/>

<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'job', action: 'edit', params: [encodedId: "${job?.encodedId}"])}'"/>
</div>

</body>
</html>