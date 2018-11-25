<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'disciplinaryRecordJudgment.entity', default: 'DisciplinaryRecordJudgment List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'DisciplinaryRecordJudgment List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
    </div></div>
</div>
<br/>
<lay:showWidget size="12" >
    <br />
    <g:set var="disciplinaryRequest" value="${disciplinaryRecordJudgment?.disciplinaryRequest}"  />

    <g:render template="/employee/employeeShowWrapper" model="[employee: disciplinaryRequest?.employee]"/>

    <lay:showWidget size="12" title="${message(code: 'request.info.label')}">
        <lay:showElement value="${disciplinaryRequest?.disciplinaryCategory}" type="DisciplinaryCategory" label="${message(code:'disciplinaryRequest.disciplinaryCategory.label',default:'disciplinaryCategory')}" />
        <lay:showElement value="${disciplinaryRequest?.requestDate}" type="ZonedDate" label="${message(code:'disciplinaryRequest.requestDate.label',default:'toDate')}" />
        <lay:showElement value="${disciplinaryRequest?.requestStatus}" type="enum" label="${message(code:'disciplinaryRequest.requestStatus.label',default:'requestStatus')}" messagePrefix="EnumRequestStatus" />
        <lay:showElement value="${disciplinaryRequest?.requestStatusNote}" type="String" label="${message(code:'disciplinaryRequest.requestStatusNote.label',default:'requestStatusNote')}" />
    </lay:showWidget>


    <lay:showWidget size="12" title="${message(code: 'disciplinaryRecordJudgment.label')}">


        <lay:showElement value="${disciplinaryRecordJudgment?.disciplinaryJudgment}" type="DisciplinaryJudgment"
                         label="${message(code:'disciplinaryRecordJudgment.disciplinaryJudgment.label',default:'disciplinaryJudgment')}" />
        <lay:showElement value="${disciplinaryRecordJudgment?.disciplinaryReasons?.toList()?.join(",")}"
                         type="String" label="${message(code:'disciplinaryRecordJudgment.disciplinaryReasons.label',default:'disciplinaryReasons')}" />

        <lay:showElement value="${disciplinaryRecordJudgment?.value}" type="String"
                         label="${message(code:'disciplinaryRecordJudgment.value.label',default:'value')}" />


        <g:if test="${disciplinaryRecordJudgment?.transientData?.currencyDTO}">
            <lay:showElement value="${disciplinaryRecordJudgment?.transientData?.currencyDTO}" type="String"
                             label="${message(code:'disciplinaryRecordJudgment.currencyId.label',default:'currencyId')}" />

        </g:if>
        <g:else>
            <lay:showElement value="${disciplinaryRecordJudgment?.transientData?.unitDTO}" type="String"
                             label="${message(code:'disciplinaryRecordJudgment.unitId.label',default:'unitId')}" />

        </g:else>


        <lay:showElement value="${disciplinaryRecordJudgment?.fromDate}" type="ZonedDate"
                         label="${message(code:'disciplinaryRecordJudgment.fromDate.label',default:'fromDate')}" />


        <lay:showElement value="${disciplinaryRecordJudgment?.toDate}" type="ZonedDate"
                         label="${message(code:'disciplinaryRecordJudgment.toDate.label',default:'toDate')}" />


        <lay:showElement value="${disciplinaryRecordJudgment?.disciplinaryListNote?.orderNo}" type="String"
                         label="${message(code:'disciplinaryRecordJudgment.orderNo.label',default:'orderNo')}" />


        <lay:showElement value="${disciplinaryRecordJudgment?.disciplinaryListNote?.note}" type="String"
                         label="${message(code:'disciplinaryRecordJudgment.note.label',default:'note')}" />



        <lay:showElement value="${disciplinaryRecordJudgment?.judgmentStatus}" type="enum"
                         label="${message(code:'disciplinaryRecordJudgment.judgmentStatus.label',default:'judgmentStatus')}" messagePrefix="EnumJudgmentStatus" />


    </lay:showWidget>

    <el:row/>



</lay:showWidget>
<el:row />

<div class="clearfix form-actions text-center">
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>