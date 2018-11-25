<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeEvaluation.entity', default: 'EmployeeEvaluation List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EmployeeEvaluation List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'employeeEvaluation',action:'list')}'"/>
    </div></div>
</div>

<el:row/><br/>
<g:render template="/employee/employeeShowWrapper" model="[employee: employeeEvaluation?.employee]"/>

<el:row/>
<el:row/>
<el:row/>

<lay:showWidget size="12" title="${title}">
    %{--<lay:showElement value="${employeeEvaluation?.evaluationTemplate}" type="EvaluationTemplate" label="${message(code:'employeeEvaluation.evaluationTemplate.label',default:'evaluationTemplate')}" />--}%
    <lay:showElement value="${employeeEvaluation?.fromDate}" type="ZonedDate" label="${message(code:'employeeEvaluation.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${employeeEvaluation?.toDate}" type="ZonedDate" label="${message(code:'employeeEvaluation.toDate.label',default:'toDate')}" />
    <lay:showElement value="${employeeEvaluation?.evaluationSum}" type="Double" label="${message(code:'employeeEvaluation.evaluationSum.label',default:'evaluationSum')}" />
    <lay:showElement value="${employeeEvaluation?.evaluationResult}" type="EvaluationCriterium" label="${message(code:'employeeEvaluation.evaluationResult.label',default:'evaluationResult')}" />
</lay:showWidget>
<el:row />

<g:if test="${employeeEvaluation?.employeeEvaluationItems}" >
    <el:row/><br/><el:row/>

    <lay:showWidget size="12" title="${message(code: 'employeeEvaluation.employeeEvaluationItems.label', default: 'evaluationItem List')}">
        <lay:table styleNumber="1">
            <lay:tableHead title="${message(code: 'employeeEvaluation.item.label')}"/>
            <lay:tableHead title="${message(code: 'evaluationItem.maxMark.label')}"/>
            <lay:tableHead title="${message(code: 'employeeEvaluation.mark.label')}"/>
            <g:each in="${employeeEvaluation?.employeeEvaluationItems?.sort { it?.evaluationItem?.index }}" var="item" status="index">
                <rowElement><tr class='center'>
                    <td class='center'>${item?.evaluationItem?.descriptionInfo?.localName}</td>
                    <td class='center'>${item?.evaluationItem?.maxMark}</td>
                    <td class='center'>${item?.mark}</td>
                </tr></rowElement>
            </g:each>
        </lay:table>
    </lay:showWidget>
    <el:row/>
    <lay:showWidget size="12">
        <lay:showElement value="${employeeEvaluation?.transientData?.note}" type="String" label="${message(code:'employeeEvaluation.note.label',default:'note')}" />
    </lay:showWidget>
    <el:row/><br/><el:row/>
</g:if>



%{--<g:if test="${employeeEvaluation?.employeeEvaluationItems}" >--}%
%{--<el:row/><br/><el:row/>--}%
    %{--<lay:panel title="${employeeEvaluation?.evaluationTemplate}" isCollapsed="false" id="${employeeEvaluation?.evaluationTemplate?.id}">--}%
    %{--<g:each in="${employeeEvaluation?.transientData?.sections}" var="section">--}%
        %{--<lay:accordion>--}%
            %{--<lay:panel title="${section?.descriptionInfo?.localName}" isCollapsed="false" id="${item?.id}">--}%
                %{--<g:each in="${section?.availableItems}" var="item" status="itemIndex">--}%

                        %{--<el:formGroup>--}%
                            %{--<el:labelField class=""--}%
                                          %{--size="6"--}%
                                          %{--isDisabled="true"--}%
                                          %{--name="answer-${item?.id}"--}%
                                          %{--value="${item?.descriptionInfo?.localName}"--}%
                                          %{--label="${item?.index}) " />--}%

                            %{--<el:labelField class=""--}%
                                          %{--isDisabled="true"--}%
                                          %{--name="mark-${item?.id}"--}%
                                          %{--size="6"--}%
                                          %{--label="${message(code:'employeeEvaluation.mark.label')}"--}%
                                          %{--value="${item?.transientData?.answer}" />--}%
                        %{--</el:formGroup>--}%

                %{--</g:each>--}%
            %{--</lay:panel>--}%
            %{--<br/>--}%
        %{--</lay:accordion>--}%
    %{--</g:each>--}%
    %{--<el:formGroup>--}%
        %{--<el:textArea--}%
                %{--size="6"--}%
                %{--name="notes"--}%
                %{--value="${employeeEvaluation?.transientData?.note}"--}%
                %{--label="${message(code:'employeeEvaluation.note.label')}" />--}%
    %{--</el:formGroup>--}%

%{--<el:row/><br/><el:row/>--}%
    %{--</lay:panel>--}%
%{--</g:if>--}%



<div class="clearfix form-actions text-center">
    <g:if test="${employeeEvaluation?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'employeeEvaluation', action: 'edit', params: [encodedId: employeeEvaluation?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true"/>
</div>


</body>
</html>