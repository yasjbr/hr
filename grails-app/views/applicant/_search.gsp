<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus" %>
<el:formGroup>
    <el:textField name="id" size="6" class=" "
                  label="${message(code: 'applicant.id.label', default: 'id')}"/>
    <el:textField name="personName" class="" size="6"
                  label="${message(code: 'applicant.personName.label', default: 'person Name')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="vacancy" action="autocomplete"
                     name="vacancy.id" label="${message(code: 'applicant.vacancy.label', default: 'vacancy')}"/>
    <div class="col-sm-6 pcp-form-control ">
        <label class="col-sm-4 control-label no-padding-right text-left">
            ${message(code: 'jobRequisition.Age.label', default: 'Age')}
        </label>

        <div class="col-sm-8">
            <div id="age" class="input-group">
                <input id="fromAge"
                       class="form-control isNumber input-integer null"
                       type="text"
                       name="fromAge">
                <span class="input-group-addon">
                    <i class="ace-icon icon-sort-numeric"></i>
                </span>
                <input id="toAge"
                       class="form-control isNumber input-integer"
                       type="text"
                       name="toAge">
            </div>
        </div>
    </div>
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="applyingDate" setMinDateFromFor="applyingDateTo"
              label="${message(code: 'applicant.applyingDate.label')}"/>



    <g:if test="${isException}">
        <el:select
                valueMessagePrefix="EnumApplicantStatus"
                from="${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.values() - ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.ACCEPTED}"
                name="applicantCurrentStatusValue"
                size="6"
                class=""
                label="${message(code: 'applicant.applicantCurrentStatus.label', default: 'applicantCurrentStatus')}"/>

        <el:hiddenField name="excludedApplicantCurrentStatusValue"
                        value="${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.ADD_TO_LIST}" type="enum"/>
        <el:hiddenField name="excludedApplicantCurrentStatusValue"
                        value="${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.ACCEPTED}" type="enum"/>
    </g:if>
    <g:elseif test="${isExceptionRecruitmentList}">
        <el:select
                valueMessagePrefix="EnumApplicantStatus"
                from="${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.values() - ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.ADD_TO_LIST - ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.TRAINING_PASSED}"
                name="applicantCurrentStatusValue"
                size="6"
                class=""
                label="${message(code: 'applicant.applicantCurrentStatus.label', default: 'applicantCurrentStatus')}"/>

        <el:hiddenField name="excludedApplicantCurrentStatusValue"
                        value="${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.ADD_TO_LIST}" type="enum"/>
        <el:hiddenField name="excludedApplicantCurrentStatusValue"
                        value="${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.TRAINING_PASSED}" type="enum"/>
    </g:elseif>
    <g:else>
        <el:select
                valueMessagePrefix="EnumApplicantStatus"
                from="${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.values()}"
                name="applicantCurrentStatusValue"
                size="6"
                class=""
                label="${message(code: 'applicant.applicantCurrentStatus.label', default: 'applicantCurrentStatus')}"/>

    </g:else>

</el:formGroup>
<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                         controller="firm" action="autocomplete"
                         name="firm.id" label="${message(code: 'firm.label', default: 'firm')}"/>
    </el:formGroup>
</sec:ifAnyGranted>

