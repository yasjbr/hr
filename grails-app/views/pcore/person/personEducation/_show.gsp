<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personEducation?.person}" type="Person" label="${message(code:'personEducation.person.label',default:'person')}" />
    <lay:showElement value="${personEducation?.educationDegree}" type="EducationDegree" label="${message(code:'personEducation.educationDegree.label',default:'educationDegree')}" />
    <lay:showElement value="${personEducation?.educationMajor}" type="EducationMajor" label="${message(code:'personEducation.educationMajor.label',default:'educationMajor')}" />

    <g:if test="${personEducation?.organization}">
        <lay:showElement value="${personEducation?.organization}" type="Organization" label="${message(code:'personEducation.organization.label',default:'organization')}" />
     </g:if>
    <g:else>
        <lay:showElement value="${personEducation?.instituteName}" type="String" label="${message(code:'personEducation.instituteName.label',default:'instituteName')}" />
    </g:else>

    <lay:showElement value="${personEducation?.educationLevel}" type="EducationLevel" label="${message(code:'personEducation.educationLevel.label',default:'educationLevel')}" />

    <lay:showElement value="${personEducation?.periodInYear}" type="PeriodInYear" label="${message(code:'personEducation.periodInYear.label',default:'PeriodInYear')}" />

    <lay:showElement value="${personEducation?.totalHours}" type="TotalHours" label="${message(code:'personEducation.totalHours.label',default:'TotalHours')}" />

    <lay:showElement value="${personEducation?.location?(personEducation?.location?.toString() + "${personEducation?.unstructuredLocation?(" - "+personEducation?.unstructuredLocation):""}"):""}" type="String" label="${message(code:'personEducation.location.label',default:'location')}" />
</lay:showWidget>