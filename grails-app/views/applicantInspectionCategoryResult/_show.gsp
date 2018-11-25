<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'ApplicantInspectionCategoryResult List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:showWidget size="12" title="${message(code: 'inspectionCategory.label', 'inspection category')}">
    <lay:showElement value="${applicantInspectionCategoryResult?.inspectionCategory?.descriptionInfo?.localName}"
                     type="InspectionCategory"
                     label="${message(code: 'applicantInspectionCategoryResult.inspectionCategory.label', default: 'inspectionCategory')}"/>
    <lay:showElement value="${applicantInspectionCategoryResult?.requestDate}" type="ZonedDate"
                     label="${message(code: 'applicantInspectionCategoryResult.requestDate.label', default: 'requestDate')}"/>
    <lay:showElement value="${applicantInspectionCategoryResult?.receiveDate}" type="ZonedDate"
                     label="${message(code: 'applicantInspectionCategoryResult.receiveDate.label', default: 'receiveDate')}"/>
    <lay:showElement value="${applicantInspectionCategoryResult?.inspectionResult}" type="enum"
                     label="${message(code: 'applicantInspectionCategoryResult.inspectionResult.label', default: 'inspectionResult')}"
                     messagePrefix="EnumInspectionResult"/>
    <lay:showElement value="${applicantInspectionCategoryResult?.resultSummary}" type="String"
                     label="${message(code: 'applicantInspectionCategoryResult.resultSummary.label', default: 'resultSummary')}"/>
</lay:showWidget>
<el:row/>

<g:if test="${applicantInspectionCategoryResult?.testsResult}">

    <lay:showWidget size="12"
                    title="${message(code: 'applicantInspectionCategoryResult.testsResult.label', default: 'testsResult')}">

        <table  class="pcpTable table table-bordered table-hover">
            <th class="center pcpHead">${message(code: 'applicantInspectionResult.inspection.label', default: 'inspection')}</th>
            <th class="center pcpHead">${message(code: 'applicantInspectionResult.sendDate.label', default: 'sendDate')}</th>
            <th class="center pcpHead">${message(code: 'applicantInspectionResult.receiveDate.label', default: 'receiveDate')}</th>
            <th class="center pcpHead">${message(code: 'applicantInspectionResult.resultValue.label', default: 'resultValue')}</th>
            <th class="center pcpHead">${message(code: 'applicantInspectionCategoryResult.resultSummary.label', default: 'resultSummary')}</th>
            <th class="center pcpHead">${message(code: 'applicantInspectionCategoryResult.executionPeriod.label', default: 'executionPeriod')}</th>
            <th class="center pcpHead">${message(code: 'applicantInspectionCategoryResult.mark.label', default: 'mark')}</th>

            <g:each in="${applicantInspectionCategoryResult?.testsResult?.sort{it.inspection.orderId}}" var="inspectionResult">
            <tr>
                <td>${inspectionResult?.inspection?.descriptionInfo?.localName?: '  '}</td>
                <td>${inspectionResult?.sendDate?.dateTime?.date?: '  '}</td>
                <td>${inspectionResult?.receiveDate?.dateTime?.date?: '  '}</td>
                <td>${inspectionResult?.resultValue?: '  '}</td>
                <td>${inspectionResult?.resultSummary?: '  '}</td>
                <td>${inspectionResult?.executionPeriod?: '  '}</td>
                <td>${inspectionResult?.mark?: '  '}</td>
            </tr>

            </g:each>

        </table>
    </lay:showWidget>

</g:if>


<el:row/>


<g:if test="${applicantInspectionCategoryResult?.committeeRoles}">

    <lay:showWidget size="12" title="${message(code: 'committeeRole.label', default: 'committee role')}">

        <g:each in="${applicantInspectionCategoryResult?.committeeRoles?.sort{it.id}}" var="committeeRoles">

            <lay:showElement label="${committeeRoles?.committeeRole?.descriptionInfo?.localName}"
                             value="${committeeRoles?.partyName}"/>

        </g:each>

    </lay:showWidget>

</g:if>


<el:row/>
</body>
</html>