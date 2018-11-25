<el:dataTable id="bordersSecurityCoordinationTable"
              searchFormName="bordersSecurityCoordinationSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="bordersSecurityCoordination" spaceBefore="true"
              hasRow="true" action="filter"
              serviceName="bordersSecurityCoordination"
              domainColumns="${domainColumns}" >

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'bordersSecurityCoordination',action: 'show')}"
                            actionParams="encodedId" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show bordersSecurityCoordination')}"/>
    </g:if>
    <g:else>

        <el:dataTableAction controller="bordersSecurityCoordination" action="show" actionParams="encodedId"
                            class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show bordersSecurityCoordination')}"/>
        <el:dataTableAction controller="bordersSecurityCoordination" action="edit" showFunction="checkRequestStatus"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit bordersSecurityCoordination')}"/>
        <el:dataTableAction controller="bordersSecurityCoordination" action="delete" showFunction="checkRequestStatus"
                            actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete bordersSecurityCoordination')}"/>


        <el:dataTableAction
                functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
                actionParams="id"
                class="blue icon-attach"
                type="function"
                message="${message(code: 'default.attachment.label')}"/>



    </g:else>

</el:dataTable>