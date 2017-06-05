package com.estudio.flex.module
{

	import com.estudio.flex.RUNTIME_GLOBAL;
	import com.estudio.flex.utils.AlertUtils;
	import com.estudio.flex.utils.ArrayUtils;
	import com.estudio.flex.utils.JSFunUtils;
	import com.estudio.flex.utils.ObjectUtils;
	import com.estudio.flex.utils.StringUtils;

	import mx.collections.ArrayCollection;

	public class FormDataService
	{

		public static const DATASET_OPERATION_TYPE_INSERT:String="i";
		public static const DATASET_OPERATION_TYPE_UPDATE:String="u";
		public static const DATASET_OPERATION_TYPE_DELETE:String="d";

		public static const DATASET_SELECTED_CHANGE_TYPE_INSERT:int=1;
		public static const DATASET_SELECTED_CHANGE_TYPE_DELETE:int=2;
		public static const DATASET_SELECTED_CHANGE_TYPE_NORMAL:int=0;

		public static const BLANK_ARRAY_COLLECTION:ArrayCollection=new ArrayCollection([]);


		private var _owner:Object=null;

		public function getOwner():Object
		{
			return _owner;
		}

		public function FormDataService(owner:Object)
		{
			_owner=owner;
		}

		////////////////////////////////////////////////////////////////////////////////////////
		private var _datasets:Array=null;

		private var _datasetStatus:Object=null;

		private var _datasetName2DataSet:Object={};

		private var _datasetValues:Object={};

		private var _datasetName2ArrayCollection:Object={};

		private var _mainDatasetName:String="";

		private var _allowAutoAppendRecord:Boolean=false;

		public function get allowAutoAppendRecord():Boolean
		{
			return _allowAutoAppendRecord;
		}

		public function set allowAutoAppendRecord(v:Boolean):void
		{
			//allowAutoAppendRecord");
			//(v);
			_allowAutoAppendRecord=v;
		}


		private static var _datasetRecordCache4Combobox:Object={};

		public function clearDynamicDataSource4ComboBox(datasetName:String, keyValue:String):void
		{
			var dataset:Object=getDataSet(datasetName);
			if (dataset.clientCache)
			{
				var key:String=dataset.cacheKey + "-" + keyValue;
				if (_datasetRecordCache4Combobox.hasOwnProperty(key))
					_datasetRecordCache4Combobox[key]=null;
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//动态加载ComboxBox列表项
		public function dynamicLoadDataSetRecords4Combobox(datasetName:String, keyValue:String, valueFieldName:String, displayFieldName:String, formParams:Object):ArrayCollection
		{
			if (StringUtils.isEmpty(keyValue))
				keyValue="NULL";
			var result:ArrayCollection=null;
			var dataset:Object=getDataSet(datasetName);
			if (dataset.clientCache)
			{
				var key:String=dataset.cacheKey + "-" + keyValue;
				if (_datasetRecordCache4Combobox.hasOwnProperty(key) && _datasetRecordCache4Combobox[key] != null)
					result=new ArrayCollection(JSON.parse(_datasetRecordCache4Combobox[key]) as Array);
			}
			if (!result)
			{
				var params:Object=ObjectUtils.mergeParams({datasetName: datasetName, keyValue: keyValue}, formParams);
				var tempResult:Object=JSFunUtils.JSFun("dynamicLoadDataSetRecords4Combobox", params);
				if (tempResult && tempResult.r)
				{
					for (var i:int=0; i < tempResult.records.length; i++)
					{
						tempResult.records[i].label=tempResult.records[i][displayFieldName];
						tempResult.records[i].data=tempResult.records[i][valueFieldName];
					}
					result=new ArrayCollection(tempResult.records);
					if (dataset.clientCache)
						_datasetRecordCache4Combobox[key]=JSON.stringify(tempResult.records);
				}
				else
				{
					result=BLANK_ARRAY_COLLECTION;
					if (dataset.clientCache)
						_datasetRecordCache4Combobox[key]="[]";
				}
			}
			return result;
		}

		////////////////////////////////////////////////////////////////////////////////////////
		//初始化DataSet定义
		public function initDefine(datasets:Array):void
		{
			this._datasets=datasets;
			for (var i:int=0; i < _datasets.length; i++)
			{
				var dataset:Object=_datasets[i];
				if (i == 0)
					_mainDatasetName=dataset.Name;
				dataset["fieldName2Index"]={};
				_datasetName2DataSet[dataset.Name]=dataset;
				if (dataset.Fields)
					for (var j:int=0; j < dataset.Fields.length; j++)
						dataset["fieldName2Index"][dataset.Fields[j]]=j;
			}
		}

		public function getDataSet(datasetName:String):Object
		{
			return _datasetName2DataSet[datasetName];
		}

		public function getDataSetIndex(datasetName:String):int
		{
			var result:int=-1;
			for (var i:int=0; i < _datasets.length; i++)
			{
				var dataset:Object=_datasets[i];
				if (dataset.Name == datasetName)
				{
					result=i;
					break;
				}
			}
			return result;
		}

		////////////////////////////////////////////////////////////////////////////////////////
		//初始化数据
		public function initFormData(data:Object, isFullInit:Boolean):void
		{
			for (var k:String in data)
			{
				_datasetValues[k]=data[k];
				_datasetName2ArrayCollection[k]=null;
			}
			initDataSetsStatus();
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//在数据集中追加一条记录
		public function appendDataSetRecord(datasetName:String, recursicveDatasets:Array=null):int
		{
			var i:int=0;
			var record:Object={};
			var dataset:Object=_datasetName2DataSet[datasetName];
			var keyField:String=dataset.Keyfield;
			if (!StringUtils.isEmpty(keyField))
			{
				var uniqueId:String="";
				var reqName:String=dataset.initFieldValue ? dataset.initFieldValue[keyField] : "";
				if (!StringUtils.isEmpty(reqName))
				{
					uniqueId=_owner.getFormParam(reqName);
					if (StringUtils.isEmpty(uniqueId) || StringUtils.equal("null", uniqueId))
					{
						uniqueId=RUNTIME_GLOBAL.getServerUniqueID();
						_owner.setFormParam(reqName, uniqueId);
					}
				}
				else
				{
					uniqueId=RUNTIME_GLOBAL.getServerUniqueID();
				}
				record[keyField]=uniqueId;
			}
			for (i=0; i < dataset.Fields.length; i++)
			{
				if (!StringUtils.equal(keyField, dataset.Fields[i]))
					record[dataset.Fields[i]]="";
			}
			var linkages:Array=dataset.Linkage;
			for (i=0; i < linkages.length; i++)
			{
				var linkage:Object=linkages[i];
				var pDatasetName:String=linkage.DS;
				var pFieldName:String=linkage.ParentField;
				var pIndex:int=_datasetStatus[pDatasetName].idx;
				if (pIndex == -1)
				{
					pIndex=appendDataSetRecord(pDatasetName, recursicveDatasets);
					if (recursicveDatasets)
						recursicveDatasets.push(pDatasetName);
				}
				record[pDatasetName + "_" + pFieldName]=getDataSetValue(pDatasetName, pFieldName);
				var linkField:String=linkage.LinkField;
				if (!StringUtils.isEmpty(linkField))
					record[linkField]=getDataSetValue(pDatasetName, pFieldName);
			}
			_datasetValues[datasetName].push(record);
			var index:int=_datasetValues[datasetName].length - 1;
			registerDataSetStatus(datasetName, DATASET_OPERATION_TYPE_INSERT, index);
			return index;
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//批量添加记录
		public function batchAppendRecords(datasetName:String, records:Array):int
		{
			var result:int=-1;
			if (!ArrayUtils.isEmpty(records))
			{
				for (var i:int=0; i < records.length; i++)
				{
					result=appendDataSetRecord(datasetName);
					var newRecord:Object=_datasetValues[datasetName][result];
					var record:Object=records[i];
					for (var k:String in record)
						newRecord[k]=record[k];
				}
			}
			return result
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//删除数据集并返回新的数据集记录集合
		public function deleteDataSetRecord(datasetName:String):void
		{
			var oldIndex:int=_datasetStatus[datasetName].idx;
			if (oldIndex != -1)
			{
				_datasetValues[datasetName][oldIndex]["__deleted__"]=true;
				registerDataSetStatus(datasetName, DATASET_OPERATION_TYPE_DELETE, oldIndex);
			}
		}

		public function clearDataSetRecords(datasetName:String):void
		{
			while (_datasetValues[datasetName].length)
			{
				_datasetValues[datasetName][0]["__deleted__"]=true;
				registerDataSetStatus(datasetName, DATASET_OPERATION_TYPE_DELETE, 0);
			}
		}

		public function deleteDataSetRecords(datasetName:String, keys:Array):void
		{
			for (var i:int=0; i < keys.length; i++)
			{
				var index:int=findDataSetRecordIndex(datasetName, keys[i]);
				if (index != -1)
				{
					_datasetValues[datasetName][index]["__deleted__"]=true;
					registerDataSetStatus(datasetName, DATASET_OPERATION_TYPE_DELETE, index);
				}
			}
		}


		////////////////////////////////////////////////////////////////////////////////////////////
		//取得DataSet主键字段名称
		public function getDataSetKeyField(datasetName:String):String
		{
			return _datasetName2DataSet[datasetName]["Keyfield"];
		}


		////////////////////////////////////////////////////////////////////////////////////////////
		//取得数据源值
		public function getDataSetValue(datasetName:String, fieldName:String):String
		{
			var result:String="";
			if (_datasetStatus.hasOwnProperty(datasetName))
			{
				var index:int=_datasetStatus[datasetName].idx;
				if (index > -1 && index < _datasetValues[datasetName].length && !StringUtils.isEmpty(fieldName))
					result=_datasetValues[datasetName][index][fieldName];
			}
			return result;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//设置数据源值
		public function setDataSetValue(datasetName:String, fieldName:String, value:String):void
		{
			if (!StringUtils.isEmpty(fieldName))
			{
				var index:int=_datasetStatus[datasetName].idx;
				if (index == -1 && _datasetValues[datasetName].length != 0)
				{
					setDataSetRecordIndex(datasetName, 0);
					index=0;
				}
				if (index == -1)
				{
					if (_allowAutoAppendRecord)
					{
						index=appendDataSetRecord(datasetName);
						_datasetValues[datasetName][index][fieldName]=value; //需要连锁增加内容
						_datasetStatus[datasetName].m=true;
					}
				}
				else
				{
					_datasetValues[datasetName][index][fieldName]=value;
					registerDataSetStatus(datasetName, DATASET_OPERATION_TYPE_UPDATE, _datasetStatus[datasetName].idx);
					_datasetStatus[datasetName].m=true;
				}

			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//取得数据集中的某条记录
		public function getDataSetRecord(datasetName:String, index:int):Object
		{
			return (index != -1) ? _datasetValues[datasetName][index] : null;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		public function getRecordIndex(datasetName:String):int
		{
			return _datasetStatus[datasetName].idx;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//取得有效数据集
		public function getDataSetRecords(datasetName:String):Array
		{
			var result:Array=[];
			var sourceArray:Array=_datasetValues[datasetName];
			for (var i:int=0; i < sourceArray.length; i++)
			{
				var record:Object=sourceArray[i];
				if (!record.__deleted__)
					result.push(record);
			}
			return result;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//更改数据集中的数据
		public function triggerDataSetChangeEvent(datasetName:String):void
		{
			this.registerDataSetStatus(datasetName, DATASET_OPERATION_TYPE_UPDATE, _datasetStatus[datasetName].idx);
		}

		///////////////////////////////////////////////////////////////////////////////////////////////
		//设置数据集记录索引
		public function setDataSetRecordIndexByItem(datasetName:String, item:Object):int
		{
			var result:int=-1;
			var dataset:Object=_datasetName2DataSet[datasetName];
			if (item != null)
				result=ArrayUtils.find(_datasetValues[datasetName], dataset["Keyfield"], item[dataset["Keyfield"]]);
			_datasetStatus[datasetName]["idx"]=result;
			return result;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////
		//设置数据集记录索引
		public function setDataSetRecordIndex(datasetName:String, index:int):int
		{
			var dataset:Object=_datasetName2DataSet[datasetName];
			_datasetStatus[datasetName]["idx"]=index;
			return index;
		}


		/////////////////////////////////////////////////////////////////////////////////////////////
		//取得索引值
		public function findDataSetRecordIndex(datasetName:String, itemKey:String):int
		{
			var result:int=-1;
			var dataset:Object=_datasetName2DataSet[datasetName];
			var keyField:String=dataset["Keyfield"];
			for (var i:int=0; i < _datasetValues[datasetName].length; i++)
			{
				if (StringUtils.equal(_datasetValues[datasetName][i][keyField], itemKey))
				{
					result=i;
					break;
				}
			}
			return result;
		}

		////////////////////////////////////////////////////////////////////////////////////////////
		//得到DataSet对应的Collection
		public function getArrayCollection(datasetName:String):ArrayCollection
		{
			var result:ArrayCollection=_datasetName2ArrayCollection[datasetName];
			if (!result)
			{
				result=new ArrayCollection(_datasetValues[datasetName]);
				_datasetName2ArrayCollection[datasetName]=result;
			}
			return result;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////
		//取得数据数组
		public function getArray(datasetName:String):Array
		{
			return _datasetValues[datasetName];
		}

		///////////////////////////////////////////////////////////////////////////////////////////////
		//设置DataSet控件值
		public function setArray(datasetName:String, records:Array):void
		{
			_datasetValues[datasetName]=records;
			if (_datasetName2ArrayCollection[datasetName])
				_datasetName2ArrayCollection[datasetName]=new ArrayCollection(records);
		}

		/////////////////////////////////////////////////////////////////////////////////////////////
		//得到更改的数据值
		internal function getModifiedDatasetsValue():Object
		{
			var result:Object=null;
			var status:Object=null;
			var records:Array=null;
			var datasetName:String=null;
			//_masterDetailDatasetCache[child.datasetName][keyValue]={records: _datasetValues[child.datasetName], status: _datasetStatus[child.datasetName]}
			for (datasetName in _datasetStatus)
			{
				status=_datasetStatus[datasetName];
				records=_datasetValues[datasetName];
				result=getModifiedDatasetValue(datasetName, status, records, result);
			}
			for (datasetName in _masterDetailDatasetCache)
			{
				var cache:Object=_masterDetailDatasetCache[datasetName];
				for (var k:String in cache)
				{
					records=cache[k].records;
					status=cache[k].status;
					result=getModifiedDatasetValue(datasetName, status, records, result);
				}
			}
			return result;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////
		//获取单个数据源数据变化值
		private function getModifiedDatasetValue(datasetName:String, status:Object, records:Array, returnResult:Object):Object
		{
			if (status.m)
			{
				var i:int=0;
				var record:Object=null;
				var datasetModifiedValue:Object={i: [], d: [], u: []};
				for (i=0; i < status.d.length; i++)
				{
					datasetModifiedValue.d.push(status.d[i]);
				}
				for (i=0; i < status.i.length; i++)
				{
					datasetModifiedValue.i.push(copyRecordButSkipNULL(records[status.i[i]]));
				}
				for (i=0; i < status.u.length; i++)
				{
					datasetModifiedValue.u.push(copyRecordButSkipNULL(records[status.u[i]]));
				}
				if (returnResult == null)
					returnResult={};
				var currentDatasetModifiedValue:Object=returnResult[datasetName];
				if (currentDatasetModifiedValue)
				{
					datasetModifiedValue.i=datasetModifiedValue.i.concat(currentDatasetModifiedValue.i);
					datasetModifiedValue.d=datasetModifiedValue.d.concat(currentDatasetModifiedValue.d);
					datasetModifiedValue.u=datasetModifiedValue.u.concat(currentDatasetModifiedValue.u);
				}
				returnResult[datasetName]=datasetModifiedValue;
			}
			return returnResult;
		}

		/////////////////////////////////////////////////////////////////////////////////////////////
		//过滤NULL
		private function copyRecordButSkipNULL(record:Object):Object
		{
			var result:Object={};
			for (var k:String in record)
			{
				if (!StringUtils.isEmpty(record[k]))
					result[k]=record[k];
			}
			return result;
		}



		////////////////////////////////////////////////////////////////////////////////////////////
		//注册数据类型的更改
		public function registerDataSetStatus(datasetName:String, type:String, index:int):void
		{
			var i:int=0;
			var status:Object=_datasetStatus[datasetName];
			var a_i:Array=status[DATASET_OPERATION_TYPE_INSERT];
			var a_d:Array=status[DATASET_OPERATION_TYPE_DELETE];
			var a_u:Array=status[DATASET_OPERATION_TYPE_UPDATE];
			if (StringUtils.equal(type, DATASET_OPERATION_TYPE_INSERT) && a_i.indexOf(index) == -1)
			{
				a_i.push(index);
				status.idx=index;
				status.m=true;
				_owner.modified=true;
			}
			else if (StringUtils.equal(type, DATASET_OPERATION_TYPE_UPDATE))
			{
				if (a_i.indexOf(index) == -1 && a_u.indexOf(index) == -1)
				{
					status[type].push(index);
					appendDataSetLinkFieldValues(datasetName, index);
					status.m=true;
				}
				_owner.modified=true;
			}
			else if (StringUtils.equal(type, DATASET_OPERATION_TYPE_DELETE))
			{
				if (a_i.indexOf(index) == -1)
				{
					a_d.push(_datasetValues[datasetName][index]);
					status.m=true;
				}
				_datasetValues[datasetName].splice(index, 1);

				var idx:int=a_i.indexOf(index);
				if (idx != -1)
					a_i.splice(idx, 1);

				idx=a_u.indexOf(index);
				if (idx != -1)
					a_u.splice(idx, 1);

				//所有顺序需要减一
				for (i=0; i < a_i.length; i++)
				{
					if (a_i[i] > index)
						a_i[i]=a_i[i] - 1;
				}

				for (i=0; i < a_u.length; i++)
				{
					if (a_u[i] > index)
						a_u[i]=a_u[i] - 1;
				}
				_owner.modified=true;
			}

		}

		////////////////////////////////////////////////////////////////////////////////////////////////
		//合并数据
		public function mergeDataSetRecord(datasetName:String, record:Object):Object
		{
			var result:Object=record;
			var index:int=setDataSetRecordIndexByItem(datasetName, record);
			if (index == -1)
			{
				_datasetValues[datasetName].push(record);
				registerDataSetStatus(datasetName, DATASET_OPERATION_TYPE_INSERT, _datasetValues[datasetName].length - 1);
			}
			else
			{
				_datasetValues[datasetName][index]=record;
				registerDataSetStatus(datasetName, DATASET_OPERATION_TYPE_UPDATE, index);
			}
			return record;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//清除Dataset更改标志
		internal function clearDatasetStatus():void
		{
			for (var k:String in _datasetStatus)
			{
				_datasetStatus[k].i=[];
				_datasetStatus[k].d=[];
				_datasetStatus[k].u=[];
				_datasetStatus[k].m=false;
			}
			clearMasterDetailCache(null, null);
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		//取得主数据源ID
		internal function getMainDatasetID():String
		{
			if (!StringUtils.isEmpty(_mainDatasetName) && _datasetStatus[_mainDatasetName].idx != -1)
				return _datasetValues[_mainDatasetName][_datasetStatus[_mainDatasetName].idx][_datasetName2DataSet[_mainDatasetName].Keyfield];
			return null;
		}

		/////////////////////////////////////////////////////////////////////////////////////////
		//记录DataSet的状态
		private function initDataSetsStatus(datasetName:String=""):void
		{
			if (!_datasetStatus)
				_datasetStatus={}
			if (StringUtils.isEmpty(datasetName))
			{
				for (var i:int=0; i < _datasets.length; i++)
				{
					var dataset:Object=_datasets[i];
					_datasetStatus[dataset.Name]={d: [], i: [], u: [], idx: Math.min(_datasetValues[dataset.Name].length - 1, 0), m: false};
				}
			}
			else
			{
				_datasetStatus[datasetName]={d: [], i: [], u: [], idx: Math.min(_datasetValues[datasetName].length - 1, 0), m: false}
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//取得字段索引位置
		private function getDataSetFieldIndex(datasetName:String, fieldName:String):int
		{
			return _datasetName2DataSet[datasetName]["fieldName2Index"][fieldName];
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		private function getDataSetRecordIndex(datasetName:String):int
		{
			return _datasetStatus[datasetName]["idx"];
		}

		///////////////////////////////////////////////////////////////////////////////
		private function appendDataSetLinkFieldValues(datasetName:String, index:int=-1):void
		{
			if (index == -1)
				index=_datasetStatus[datasetName].idx;
			var dataset:Object=_datasetName2DataSet[datasetName];
			var linkages:Array=dataset.Linkage;
			for (var i:int=0; i < linkages.length; i++)
			{
				var record:Object=_datasetValues[datasetName][index];
				var linkage:Object=linkages[i];
				var pDatasetName:String=linkage.DS;
				var pFieldName:String=linkage.ParentField;
				var pIndex:int=_datasetStatus[pDatasetName].idx;
				record[pDatasetName + "_" + pFieldName]=getDataSetValue(pDatasetName, pFieldName);
				var linkField:String=linkage.LinkField;
				if (!StringUtils.isEmpty(linkField))
					record[linkField]=getDataSetValue(pDatasetName, pFieldName);
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		public function get hasRecords():Boolean
		{
			return getMainDatasetID() != null;
		}

		/////////////////////////////////////////////////////////////////////////////////////////
		//获取DataSet的参数值 用于动态加载数据
		public function getDataSetParams4DynamicLoadData(datasetName:String):Object
		{
			var params:Object={};
			var dataset:Object=_datasetName2DataSet[datasetName];
			var linkages:Array=dataset.Linkage;
			for (var i:int=0; i < linkages.length; i++)
			{
				var linkage:Object=linkages[i];
				var pDatasetName:String=linkage.DS;
				var pFieldName:String=linkage.ParentField;
				var paramValue:String=getDataSetValue(pDatasetName, pFieldName);
				if (!params[pDatasetName])
					params[pDatasetName]={};
				params[pDatasetName][pFieldName]=paramValue;
			}
			return params;
		}

		///////////////////////////////////////////////////////////////////////////////////////////
		//动态加载数据
		private var dynamicLoadDatasetCache:Object={};

		public function dynamicLoadFormDataSetRecord(dataset2Params:Array):Boolean
		{
			var result:Boolean=false;
			var key:String=StringUtils.SHA1(JSON.stringify(dataset2Params));
			var data:Object=dynamicLoadDatasetCache[key];
			if (!data)
			{
				data=JSFunUtils.JSFun("dynamicLoadFormDataSetRecord", dataset2Params);
				dynamicLoadDatasetCache[key]=data;
			}
			if (data != null && data.r)
			{
				for (var i:int=0; i < dataset2Params.length; i++)
				{
					var datasetName:String=dataset2Params[i]["datasetName"];
					setArray(datasetName, data[datasetName]);
				}
				result=true;
			}
			return result;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////
		//获取子数据源列表
		public function getChildrenDatasets(parentDatasetName:String, datasetList:Array, isRecursize:Boolean=true):Boolean
		{
			for (var datasetName:String in _datasetName2DataSet)
			{
				var dataset:Object=_datasetName2DataSet[datasetName];
				var linkages:Array=dataset.Linkage;
				for (var i:int=0; i < linkages.length; i++)
				{
					var linkage:Object=linkages[i];
					var pDatasetName:String=linkage.DS;
					if (parentDatasetName == pDatasetName)
					{
						datasetList.push({datasetName: datasetName, pDatasetName: pDatasetName, pFieldName: linkage.ParentField, initFieldName: linkage.InitField, initDataSetName: linkage.InitDS});
						if (isRecursize)
							getChildrenDatasets(datasetName, datasetList);
					}
				}
			}
			return datasetList.length != 0;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////
		//判断datasetName 是否被 parentDatasetName 初始化
		public function isFirstInitByOtherDataset(datasetName:String, parentDatasetName:String):Boolean
		{
			var result:Boolean=false;
			var dataset:Object=_datasetName2DataSet[datasetName];
			var linkages:Array=dataset.Linkage;
			for (var i:int=0; i < linkages.length; i++)
			{
				var linkage:Object=linkages[i];
				if (StringUtils.equal(parentDatasetName, linkage.InitDS))
				{
					result=true;
					break;
				}
			}
			return result;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////
		//动态加载数据
		private var dataset2ParentDataSetList:Object={};

		public function dynamicLoadDataSetRecordsByParentDataSource(parentDataSource:String, isParamValueFromInitDataset:Boolean, isCachedAble:Boolean):Object
		{
			var data:Object=null;
			var childDataSets:Array=dataset2ParentDataSetList[parentDataSource];
			if (!childDataSets)
			{
				childDataSets=[];
				getChildrenDatasets(parentDataSource, childDataSets);
				dataset2ParentDataSetList[parentDataSource]=childDataSets;
			}
			return childDataSets.length == 0 ? null : dynamicLoadDatasetRecordsByParams(childDataSets, isParamValueFromInitDataset, isCachedAble);
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		public function dynamicLoadDatasetRecords(datasetList:Array, isParamValueFromInitDataset:Boolean, isCachedAble:Boolean):Object
		{
			var paramSets:Array=[];
			for (var i:int=0; i < datasetList.length; i++)
			{
				var dataset:Object=_datasetName2DataSet[datasetList[i]];
				var linkages:Array=dataset.Linkage;
				for (var j:int=0; j < linkages.length; j++)
				{
					var linkage:Object=linkages[j];
					paramSets.push({datasetName: datasetList[i], pDatasetName: linkage.DS, pFieldName: linkage.ParentField, initFieldName: linkage.InitField, initDataSetName: linkage.InitDS});
				}
			}
			return dynamicLoadDatasetRecordsByParams(paramSets, isParamValueFromInitDataset, isCachedAble);
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//动态加载数据
		private var dynamicLoadDatasetRecordsCache:Object={};

		private function dynamicLoadDatasetRecordsByParams(loadDatasetParams:Array, isParamValueFromInitDataset:Boolean, isCachedAble:Boolean):Object
		{
			var params:Object={};
			var datasetNames:Array=[];
			for (var i:int=0; i < loadDatasetParams.length; i++)
			{
				var child:Object=loadDatasetParams[i];
				var pDatasetName:String=child.pDatasetName;
				var pFieldName:String=child.pFieldName;
				var initDatasetName:String=child.initDataSetName;
				var initFieldName:String=child.initFieldName;
				var paramValue:String="";

				if (isParamValueFromInitDataset && !StringUtils.isEmpty(initDatasetName) && !StringUtils.isEmpty(initFieldName))
					paramValue=getDataSetValue(initDatasetName, initFieldName);
				else if (ArrayUtils.indexOf(datasetNames, pDatasetName) == -1)
					paramValue=getDataSetValue(pDatasetName, pFieldName);

				datasetNames.push(child.datasetName);

				if (!StringUtils.isEmpty(paramValue))
				{
					if (!params[pDatasetName])
						params[pDatasetName]={};
					params[pDatasetName][pFieldName]=paramValue;
				}
			}
			if (isCachedAble)
			{
				var key:String=StringUtils.SHA1(JSON.stringify(params));
				if (!dynamicLoadDatasetRecordsCache[key])
					dynamicLoadDatasetRecordsCache[key]={returnData: JSFunUtils.JSFun("dynamicLoadFormDatasetRecords", datasetNames, params), datasetList: datasetNames};
				return dynamicLoadDatasetRecordsCache[key];
			}
			return {returnData: JSFunUtils.JSFun("dynamicLoadFormDatasetRecords", datasetNames, params), datasetList: datasetNames};
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//表单数据缓存问题
		private var _masterDetailDatasetCache:Object={};
		private var _datasetChildCache:Object={};

		//获取直系子表
		private function getMasterDetailChildren(datasetName:String):Array
		{
			var childs:Array=_datasetChildCache[datasetName];
			if (childs == null)
			{
				childs=[];
				getChildrenDatasets(datasetName, childs, false);
				_datasetChildCache[datasetName]=childs;
			}
			return childs;
		}

		//缓存数据
		public function cacheMasterDetailRecords(parentDatasetName:String, keyValue:String):void
		{
			var childs:Array=getMasterDetailChildren(parentDatasetName);
			for (var i:int=0; i < childs.length; i++)
			{
				var child:Object=childs[i];
				var childDatasetName:String=child.datasetName;
				var childParentField:String=child.pFieldName;
				if (!_masterDetailDatasetCache[child.datasetName])
					_masterDetailDatasetCache[child.datasetName]={};
				_masterDetailDatasetCache[child.datasetName][keyValue]={records: _datasetValues[child.datasetName], status: _datasetStatus[child.datasetName]};
				var tempKeyValue:String=getDataSetValue(child.datasetName, getDataSetKeyField(child.datasetName));
				if (!StringUtils.isEmpty(tempKeyValue))
					cacheMasterDetailRecords(child.datasetName, tempKeyValue);
					//_datasetStatus[dataset.Name]={d: [], i: [], u: [], idx: Math.min(_datasetValues[dataset.Name].length - 1, 0), m: false}
			}
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//清除缓存数据
		public function clearMasterDetailCache(parentDatasetName:String, keyValue:String):void
		{
			if (StringUtils.isEmpty(parentDatasetName) && StringUtils.isEmpty(keyValue))
			{
				_masterDetailDatasetCache={};
			}
			else
			{
				var childs:Array=getMasterDetailChildren(parentDatasetName);
				for (var i:int=0; i < childs.length; i++)
				{
					var child:Object=childs[i];
					var datasetName:String=childs[i].datasetName;
					var hasCache:Boolean=_masterDetailDatasetCache[datasetName] && _masterDetailDatasetCache[datasetName][keyValue];
					if (!hasCache)
						continue;
					var childRecords:Array=_masterDetailDatasetCache[datasetName][keyValue].records;
					var childKeyFieldName:String=getDataSetKeyField(datasetName);
					for (var j:int=0; j < childRecords.length; j++)
						clearMasterDetailCache(datasetName, childRecords[j][childKeyFieldName]);
					_masterDetailDatasetCache[datasetName][keyValue]=null;
					delete _masterDetailDatasetCache[datasetName][keyValue];
				}
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		public function getMasterDetailRecords(parentDatasetName:String, keyValue:String, datasets:Array, isNew:Boolean=false):void
		{
			var childs:Array=getMasterDetailChildren(parentDatasetName);
			if (childs.length == 0)
				return;

			var i:int=0;
			var datasetName:String=null;

			if (isNew || _datasetStatus[parentDatasetName].idx == -1)
			{
				for (i=0; i < childs.length; i++)
				{
					datasetName=childs[i].datasetName;
					setArray(datasetName, []);
					initDataSetsStatus(datasetName);
					datasets.push(datasetName);
					getMasterDetailRecords(datasetName, null, datasets, isNew);
				}
			}
			else
			{
				var hasCache:Boolean=_masterDetailDatasetCache[childs[0].datasetName] && _masterDetailDatasetCache[childs[0].datasetName][keyValue];
				if (hasCache)
				{
					for (i=0; i < childs.length; i++)
					{
						var child:Object=childs[i];
						datasetName=childs[i].datasetName;
						var cache:Object=_masterDetailDatasetCache[datasetName][keyValue];
						setArray(datasetName, cache.records);
						_datasetStatus[datasetName]=cache.status;
						_masterDetailDatasetCache[datasetName][keyValue]=null;
						delete _masterDetailDatasetCache[datasetName][keyValue];
						//_datasetName2ArrayCollection[child.datasetName]=null;
						datasets.push(datasetName);
						getMasterDetailRecords(datasetName, getDataSetValue(datasetName, getDataSetKeyField(datasetName)), datasets, isNew);
					}
				}
				else //从数据库加载
				{
					var temp:Object=dynamicLoadDataSetRecordsByParentDataSource(parentDatasetName, false, false);
					var returnData:Object=temp.returnData;
					if (!(returnData && returnData.r))
					{
						AlertUtils.alert("从服务器加载数据失败，原因:" + returnData.msg, AlertUtils.ALERT_STOP);
						return;
					}
					var datasetList:Array=temp.datasetList;
					for (i=0; i < datasetList.length; i++)
					{
						datasetName=datasetList[i];
						////(JSON.stringify(returnData[datasetName]));
						setArray(datasetName, returnData[datasetName]);
						//_datasetName2ArrayCollection[datasetName]=null;
						initDataSetsStatus(datasetName);
						datasets.push(datasetName);
					}
				}
			}
		}

		//是否是主数据源
		public function isMainDatabase(name:String):Boolean
		{
			return _mainDatasetName == name;
		}

		public function getDatasets():Array
		{
			return _datasets;
		}

		public function isNew():Boolean
		{
			return true;
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	} //end class
} //end package

