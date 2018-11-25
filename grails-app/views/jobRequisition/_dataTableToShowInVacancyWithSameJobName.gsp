<el:dataTable id="jobRequisitionTableForNumberOfPosition" searchFormName="jobRequisitionWithSameJobTitleName"
              title=""
              dataTableTitle="${message(code: 'jobRequisition.theSame.label')}"
              hasCheckbox="false"
              widthClass="col-sm-12"
              controller="jobRequisition"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="jobRequisition">
   </el:dataTable>