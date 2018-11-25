<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'loanNotice.entity', default: 'loanNotice')}" />
    <g:set var="tabEntity" value="${message(code: 'loanNoticeReplayRequest.entity', default: 'loanNoticeReplayRequest')}" />
    <g:set var="tabEntities" value="${message(code: 'loanNoticeReplayRequest.entities', default: 'loanNoticeReplayRequest')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list loanNoticeReplayRequest')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create loanNoticeReplayRequest')}" />
    <lay:collapseWidget id="loanNoticeReplayRequestCollapseWidget" icon="icon-search"
                        title="${message(code:'default.search.label',args:[tabEntities])}"
                        size="12" collapsed="true" data-toggle="collapse" >
        <lay:widgetBody>
            <el:form action="#" name="loanNoticeReplayRequestSearchForm">
                <el:hiddenField name="loanNotice.encodedId" value="${entityId}" />
                <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
                <g:render template="/loanNoticeReplayRequest/search" model="[:]"/>
                <el:formButton functionName="search" onClick="_dataTables['loanNoticeReplayRequestTable'].draw()"/>
                <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('loanNoticeReplayRequestSearchForm');_dataTables['loanNoticeReplayRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <g:render template="/loanNoticeReplayRequest/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity,hasAttachment:true,domainColumns:'DOMAIN_TAB_COLUMNS']"/>


    <g:if test="${loanNotice?.loanNoticeStatus == ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus.UNDER_NOMINATION}">

        <div class="clearfix form-actions text-center">
            <btn:createButton class="btn btn-sm btn-info2"
                              accessUrl="${createLink(controller: tabEntityName, action: 'create')}"
                              onclick="renderInLineCreate()"
                              label="${tabTitle}">
                <i class="icon-plus"></i>
            </btn:createButton>
        </div>
    </g:if>
</div>