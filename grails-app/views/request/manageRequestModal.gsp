<el:validatableModalForm title="${message(code: 'request.info.label')}"
                         name="requestForm" width="70%"
                         callBackFunction="callBackFunction"
                         controller="workflowPathDetails"
                         action="update" >

    <msg:modal/>


    <el:hiddenField name="processedBy" value="${ps.police.common.utils.v1.PCPSessionUtils.getValue("personId")}"/>
    <g:render template="/${domainName}/show" model="${request + [hide: true]}"/>


    <el:row/>
    <lay:widget size="12" transparent="true" color="blue" icon="icon-ok-circled"
                title="${message(code: 'workflow.requiredApprovalFromFirm.label')}">

        <lay:widgetBody>
            <el:hiddenField name="workflowPathHeader.id" value="${workflowPathHeader?.id}"/>
            <g:each in="${workflowPathHeader?.workflowPathDetails?.sort { it.sequence }}" var="${workflowPathDetails}"
                    status="index">

                <el:row>
                %{--show the  approved on request--}%
                    <g:if test="${workflowPathDetails?.workflowStatus == ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus.APPROVED}">

                        <lay:showWidget size="12" title="${workflowPathDetails?.transientData?.toJobTitleName}">

                            <lay:showElement
                                    value="${workflowPathDetails?.transientData?.personDTO?.localFullName}"
                                    size="4"
                                    label="${message(code: 'employee.label', default: 'employee')}"/>

                            <lay:showElement
                                    value="${message(code: 'EnumWorkflowStatus.' + workflowPathDetails?.workflowStatus, default: "${workflowPathDetails?.workflowStatus}")}"
                                    size="4"
                                    label="${message(code: 'default.action', default: 'action')}"/>

                            <lay:showElement
                                    value="${workflowPathDetails?.note}"
                                    size="4"
                                    label="${message(code: 'workflow.note.label', default: 'note')}"/>

                        </lay:showWidget>
                        <el:row/>
                        <el:row/>
                        <el:row/>

                    </g:if>


                %{--action form for current user --}%
                    <g:if test="${ps.police.common.utils.v1.PCPSessionUtils.getValue("jobTitleId") == workflowPathDetails?.toJobTitle && workflowPathDetails?.workflowStatus in [ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus.WAIT_FOR_APPROVAL, ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus.NOT_SEEN]}">
                        <el:formGroup>
                            <el:hiddenField name="id" value="${workflowPathDetails?.id}"/>
                            <el:hiddenField name="jobTitle" value="${workflowPathDetails?.toJobTitle}"/>
                            <g:render template="/workflowPathDetails/manage"
                                      model="[workflowPathDetails: workflowPathDetails]"/>
                        </el:formGroup>
                    </g:if>

                </el:row>
            </g:each>
        </lay:widgetBody>
    </lay:widget>

    <el:formButton isSubmit="true" functionName="save"/>
</el:validatableModalForm>

<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            window.location.reload();
        }
    }
</script>