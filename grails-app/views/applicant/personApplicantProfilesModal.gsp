<el:modal isModalWithDiv="true"  id="previousPersonApplicantsModal" title="${message(code:'applicant.entities')}" preventCloseOutSide="true" width="80%">
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'Applicant List')}"/>
    <g:if test="${params.boolean("anyOpenApplicantExist")}">
        <msg:error label="${message(code:'applicant.hasOldOpenApplicant.error.label')}" />
    </g:if>
    <g:else>
        <msg:warning label="${message(code:'applicant.hasOldApplicant.error.label')}" />
        <btn:button message="${message(code:'applicant.createAnotherApplicantForSamePerson.error.label')}"
                onClick="window.location.href='${createLink(controller: 'applicant', action: 'createNewApplicant', params: [personId:params.personId,createAnotherApplicant:true])}'"/>
    </g:else>
    <el:form action="#" name="previousPersonApplicantsSearchForm" style="display: none;">
        <el:hiddenField name="personId" value="${params.personId}"/>
    </el:form>
    <el:dataTable id="disciplinaryRequestTable" searchFormName="previousPersonApplicantsSearchForm"
                  hasCheckbox="false" widthClass="col-sm-12" controller="applicant"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="applicant" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
        <el:dataTableAction controller="applicant" action="show" actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show applicant')}"/>
    </el:dataTable>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize();
</script>