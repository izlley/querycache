//
// Autogenerated by Thrift Compiler (0.9.1)
//
// DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
//


TTypeId = {
'UNKNOWN' : 0,
'UNSUPPORTED' : 1,
'BOOLEAN' : 2,
'TINYINT' : 3,
'SMALLINT' : 4,
'INT' : 5,
'BIGINT' : 6,
'FLOAT' : 7,
'DOUBLE' : 8,
'DATE' : 9,
'DATETIME' : 10,
'TIMESTAMP' : 11,
'STRING' : 12,
'BINARY' : 13,
'DECIMAL' : 14,
'CHAR' : 15,
'ARRAY' : 16,
'MAP' : 17,
'STRUCT' : 18,
'UNION' : 19,
'USER_DEFINED' : 20
};
TPrimitiveTypeEntry = function(args) {
  this.type = null;
  this.len = null;
  this.position = null;
  this.scale = null;
  if (args) {
    if (args.type !== undefined) {
      this.type = args.type;
    }
    if (args.len !== undefined) {
      this.len = args.len;
    }
    if (args.position !== undefined) {
      this.position = args.position;
    }
    if (args.scale !== undefined) {
      this.scale = args.scale;
    }
  }
};
TPrimitiveTypeEntry.prototype = {};
TPrimitiveTypeEntry.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.I32) {
        this.type = input.readI32().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 2:
      if (ftype == Thrift.Type.I32) {
        this.len = input.readI32().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 3:
      if (ftype == Thrift.Type.I32) {
        this.position = input.readI32().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 4:
      if (ftype == Thrift.Type.I32) {
        this.scale = input.readI32().value;
      } else {
        input.skip(ftype);
      }
      break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

TPrimitiveTypeEntry.prototype.write = function(output) {
  output.writeStructBegin('TPrimitiveTypeEntry');
  if (this.type !== null && this.type !== undefined) {
    output.writeFieldBegin('type', Thrift.Type.I32, 1);
    output.writeI32(this.type);
    output.writeFieldEnd();
  }
  if (this.len !== null && this.len !== undefined) {
    output.writeFieldBegin('len', Thrift.Type.I32, 2);
    output.writeI32(this.len);
    output.writeFieldEnd();
  }
  if (this.position !== null && this.position !== undefined) {
    output.writeFieldBegin('position', Thrift.Type.I32, 3);
    output.writeI32(this.position);
    output.writeFieldEnd();
  }
  if (this.scale !== null && this.scale !== undefined) {
    output.writeFieldBegin('scale', Thrift.Type.I32, 4);
    output.writeI32(this.scale);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

TArrayTypeEntry = function(args) {
  this.objectTypePtr = null;
  if (args) {
    if (args.objectTypePtr !== undefined) {
      this.objectTypePtr = args.objectTypePtr;
    }
  }
};
TArrayTypeEntry.prototype = {};
TArrayTypeEntry.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.I32) {
        this.objectTypePtr = input.readI32().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

TArrayTypeEntry.prototype.write = function(output) {
  output.writeStructBegin('TArrayTypeEntry');
  if (this.objectTypePtr !== null && this.objectTypePtr !== undefined) {
    output.writeFieldBegin('objectTypePtr', Thrift.Type.I32, 1);
    output.writeI32(this.objectTypePtr);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

TMapTypeEntry = function(args) {
  this.keyTypePtr = null;
  this.valueTypePtr = null;
  if (args) {
    if (args.keyTypePtr !== undefined) {
      this.keyTypePtr = args.keyTypePtr;
    }
    if (args.valueTypePtr !== undefined) {
      this.valueTypePtr = args.valueTypePtr;
    }
  }
};
TMapTypeEntry.prototype = {};
TMapTypeEntry.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.I32) {
        this.keyTypePtr = input.readI32().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 2:
      if (ftype == Thrift.Type.I32) {
        this.valueTypePtr = input.readI32().value;
      } else {
        input.skip(ftype);
      }
      break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

TMapTypeEntry.prototype.write = function(output) {
  output.writeStructBegin('TMapTypeEntry');
  if (this.keyTypePtr !== null && this.keyTypePtr !== undefined) {
    output.writeFieldBegin('keyTypePtr', Thrift.Type.I32, 1);
    output.writeI32(this.keyTypePtr);
    output.writeFieldEnd();
  }
  if (this.valueTypePtr !== null && this.valueTypePtr !== undefined) {
    output.writeFieldBegin('valueTypePtr', Thrift.Type.I32, 2);
    output.writeI32(this.valueTypePtr);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

TStructTypeEntry = function(args) {
  this.nameToTypePtr = null;
  if (args) {
    if (args.nameToTypePtr !== undefined) {
      this.nameToTypePtr = args.nameToTypePtr;
    }
  }
};
TStructTypeEntry.prototype = {};
TStructTypeEntry.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.MAP) {
        var _size0 = 0;
        var _rtmp34;
        this.nameToTypePtr = {};
        var _ktype1 = 0;
        var _vtype2 = 0;
        _rtmp34 = input.readMapBegin();
        _ktype1 = _rtmp34.ktype;
        _vtype2 = _rtmp34.vtype;
        _size0 = _rtmp34.size;
        for (var _i5 = 0; _i5 < _size0; ++_i5)
        {
          if (_i5 > 0 ) {
            if (input.rstack.length > input.rpos[input.rpos.length -1] + 1) {
              input.rstack.pop();
            }
          }
          var key6 = null;
          var val7 = null;
          key6 = input.readString().value;
          val7 = input.readI32().value;
          this.nameToTypePtr[key6] = val7;
        }
        input.readMapEnd();
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

TStructTypeEntry.prototype.write = function(output) {
  output.writeStructBegin('TStructTypeEntry');
  if (this.nameToTypePtr !== null && this.nameToTypePtr !== undefined) {
    output.writeFieldBegin('nameToTypePtr', Thrift.Type.MAP, 1);
    output.writeMapBegin(Thrift.Type.STRING, Thrift.Type.I32, Thrift.objectLength(this.nameToTypePtr));
    for (var kiter8 in this.nameToTypePtr)
    {
      if (this.nameToTypePtr.hasOwnProperty(kiter8))
      {
        var viter9 = this.nameToTypePtr[kiter8];
        output.writeString(kiter8);
        output.writeI32(viter9);
      }
    }
    output.writeMapEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

TUnionTypeEntry = function(args) {
  this.nameToTypePtr = null;
  if (args) {
    if (args.nameToTypePtr !== undefined) {
      this.nameToTypePtr = args.nameToTypePtr;
    }
  }
};
TUnionTypeEntry.prototype = {};
TUnionTypeEntry.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.MAP) {
        var _size10 = 0;
        var _rtmp314;
        this.nameToTypePtr = {};
        var _ktype11 = 0;
        var _vtype12 = 0;
        _rtmp314 = input.readMapBegin();
        _ktype11 = _rtmp314.ktype;
        _vtype12 = _rtmp314.vtype;
        _size10 = _rtmp314.size;
        for (var _i15 = 0; _i15 < _size10; ++_i15)
        {
          if (_i15 > 0 ) {
            if (input.rstack.length > input.rpos[input.rpos.length -1] + 1) {
              input.rstack.pop();
            }
          }
          var key16 = null;
          var val17 = null;
          key16 = input.readString().value;
          val17 = input.readI32().value;
          this.nameToTypePtr[key16] = val17;
        }
        input.readMapEnd();
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

TUnionTypeEntry.prototype.write = function(output) {
  output.writeStructBegin('TUnionTypeEntry');
  if (this.nameToTypePtr !== null && this.nameToTypePtr !== undefined) {
    output.writeFieldBegin('nameToTypePtr', Thrift.Type.MAP, 1);
    output.writeMapBegin(Thrift.Type.STRING, Thrift.Type.I32, Thrift.objectLength(this.nameToTypePtr));
    for (var kiter18 in this.nameToTypePtr)
    {
      if (this.nameToTypePtr.hasOwnProperty(kiter18))
      {
        var viter19 = this.nameToTypePtr[kiter18];
        output.writeString(kiter18);
        output.writeI32(viter19);
      }
    }
    output.writeMapEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

TUserDefinedTypeEntry = function(args) {
  this.typeClassName = null;
  if (args) {
    if (args.typeClassName !== undefined) {
      this.typeClassName = args.typeClassName;
    }
  }
};
TUserDefinedTypeEntry.prototype = {};
TUserDefinedTypeEntry.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.STRING) {
        this.typeClassName = input.readString().value;
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

TUserDefinedTypeEntry.prototype.write = function(output) {
  output.writeStructBegin('TUserDefinedTypeEntry');
  if (this.typeClassName !== null && this.typeClassName !== undefined) {
    output.writeFieldBegin('typeClassName', Thrift.Type.STRING, 1);
    output.writeString(this.typeClassName);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

TTypeEntry = function(args) {
  this.primitiveEntry = null;
  this.arrayEntry = null;
  this.mapEntry = null;
  this.structEntry = null;
  this.unionEntry = null;
  this.userDefinedTypeEntry = null;
  if (args) {
    if (args.primitiveEntry !== undefined) {
      this.primitiveEntry = args.primitiveEntry;
    }
    if (args.arrayEntry !== undefined) {
      this.arrayEntry = args.arrayEntry;
    }
    if (args.mapEntry !== undefined) {
      this.mapEntry = args.mapEntry;
    }
    if (args.structEntry !== undefined) {
      this.structEntry = args.structEntry;
    }
    if (args.unionEntry !== undefined) {
      this.unionEntry = args.unionEntry;
    }
    if (args.userDefinedTypeEntry !== undefined) {
      this.userDefinedTypeEntry = args.userDefinedTypeEntry;
    }
  }
};
TTypeEntry.prototype = {};
TTypeEntry.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.STRUCT) {
        this.primitiveEntry = new TPrimitiveTypeEntry();
        this.primitiveEntry.read(input);
      } else {
        input.skip(ftype);
      }
      break;
      case 2:
      if (ftype == Thrift.Type.STRUCT) {
        this.arrayEntry = new TArrayTypeEntry();
        this.arrayEntry.read(input);
      } else {
        input.skip(ftype);
      }
      break;
      case 3:
      if (ftype == Thrift.Type.STRUCT) {
        this.mapEntry = new TMapTypeEntry();
        this.mapEntry.read(input);
      } else {
        input.skip(ftype);
      }
      break;
      case 4:
      if (ftype == Thrift.Type.STRUCT) {
        this.structEntry = new TStructTypeEntry();
        this.structEntry.read(input);
      } else {
        input.skip(ftype);
      }
      break;
      case 5:
      if (ftype == Thrift.Type.STRUCT) {
        this.unionEntry = new TUnionTypeEntry();
        this.unionEntry.read(input);
      } else {
        input.skip(ftype);
      }
      break;
      case 6:
      if (ftype == Thrift.Type.STRUCT) {
        this.userDefinedTypeEntry = new TUserDefinedTypeEntry();
        this.userDefinedTypeEntry.read(input);
      } else {
        input.skip(ftype);
      }
      break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

TTypeEntry.prototype.write = function(output) {
  output.writeStructBegin('TTypeEntry');
  if (this.primitiveEntry !== null && this.primitiveEntry !== undefined) {
    output.writeFieldBegin('primitiveEntry', Thrift.Type.STRUCT, 1);
    this.primitiveEntry.write(output);
    output.writeFieldEnd();
  }
  if (this.arrayEntry !== null && this.arrayEntry !== undefined) {
    output.writeFieldBegin('arrayEntry', Thrift.Type.STRUCT, 2);
    this.arrayEntry.write(output);
    output.writeFieldEnd();
  }
  if (this.mapEntry !== null && this.mapEntry !== undefined) {
    output.writeFieldBegin('mapEntry', Thrift.Type.STRUCT, 3);
    this.mapEntry.write(output);
    output.writeFieldEnd();
  }
  if (this.structEntry !== null && this.structEntry !== undefined) {
    output.writeFieldBegin('structEntry', Thrift.Type.STRUCT, 4);
    this.structEntry.write(output);
    output.writeFieldEnd();
  }
  if (this.unionEntry !== null && this.unionEntry !== undefined) {
    output.writeFieldBegin('unionEntry', Thrift.Type.STRUCT, 5);
    this.unionEntry.write(output);
    output.writeFieldEnd();
  }
  if (this.userDefinedTypeEntry !== null && this.userDefinedTypeEntry !== undefined) {
    output.writeFieldBegin('userDefinedTypeEntry', Thrift.Type.STRUCT, 6);
    this.userDefinedTypeEntry.write(output);
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

TTypeDesc = function(args) {
  this.types = null;
  if (args) {
    if (args.types !== undefined) {
      this.types = args.types;
    }
  }
};
TTypeDesc.prototype = {};
TTypeDesc.prototype.read = function(input) {
  input.readStructBegin();
  while (true)
  {
    var ret = input.readFieldBegin();
    var fname = ret.fname;
    var ftype = ret.ftype;
    var fid = ret.fid;
    if (ftype == Thrift.Type.STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
      if (ftype == Thrift.Type.LIST) {
        var _size20 = 0;
        var _rtmp324;
        this.types = [];
        var _etype23 = 0;
        _rtmp324 = input.readListBegin();
        _etype23 = _rtmp324.etype;
        _size20 = _rtmp324.size;
        for (var _i25 = 0; _i25 < _size20; ++_i25)
        {
          var elem26 = null;
          elem26 = new TTypeEntry();
          elem26.read(input);
          this.types.push(elem26);
        }
        input.readListEnd();
      } else {
        input.skip(ftype);
      }
      break;
      case 0:
        input.skip(ftype);
        break;
      default:
        input.skip(ftype);
    }
    input.readFieldEnd();
  }
  input.readStructEnd();
  return;
};

TTypeDesc.prototype.write = function(output) {
  output.writeStructBegin('TTypeDesc');
  if (this.types !== null && this.types !== undefined) {
    output.writeFieldBegin('types', Thrift.Type.LIST, 1);
    output.writeListBegin(Thrift.Type.STRUCT, this.types.length);
    for (var iter27 in this.types)
    {
      if (this.types.hasOwnProperty(iter27))
      {
        iter27 = this.types[iter27];
        iter27.write(output);
      }
    }
    output.writeListEnd();
    output.writeFieldEnd();
  }
  output.writeFieldStop();
  output.writeStructEnd();
  return;
};

PRIMITIVE_TYPES = [2,3,4,5,6,7,8,12,9,10,11,13,15,14];
COMPLEX_TYPES = [16,17,18,19,20];
COLLECTION_TYPES = [16,17];
TYPE_NAMES = {2 : 'BOOLEAN',
3 : 'TINYINT',
4 : 'SMALLINT',
5 : 'INT',
6 : 'BIGINT',
7 : 'FLOAT',
8 : 'DOUBLE',
12 : 'STRING',
9 : 'DATE',
10 : 'DATETIME',
11 : 'TIMESTAMP',
13 : 'BINARY',
15 : 'CHAR',
14 : 'DECIMAL',
16 : 'ARRAY',
17 : 'MAP',
18 : 'STRUCT',
19 : 'UNION'
};