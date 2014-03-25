/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef ReverseOrderService_H
#define ReverseOrderService_H

#include <thrift/TDispatchProcessor.h>
#include "DebugProtoTest_types.h"

namespace thrift { namespace test { namespace debug {

class ReverseOrderServiceIf {
 public:
  virtual ~ReverseOrderServiceIf() {}
  virtual void myMethod(const std::string& first, const int16_t second, const int32_t third, const int64_t fourth) = 0;
};

class ReverseOrderServiceIfFactory {
 public:
  typedef ReverseOrderServiceIf Handler;

  virtual ~ReverseOrderServiceIfFactory() {}

  virtual ReverseOrderServiceIf* getHandler(const ::apache::thrift::TConnectionInfo& connInfo) = 0;
  virtual void releaseHandler(ReverseOrderServiceIf* /* handler */) = 0;
};

class ReverseOrderServiceIfSingletonFactory : virtual public ReverseOrderServiceIfFactory {
 public:
  ReverseOrderServiceIfSingletonFactory(const boost::shared_ptr<ReverseOrderServiceIf>& iface) : iface_(iface) {}
  virtual ~ReverseOrderServiceIfSingletonFactory() {}

  virtual ReverseOrderServiceIf* getHandler(const ::apache::thrift::TConnectionInfo&) {
    return iface_.get();
  }
  virtual void releaseHandler(ReverseOrderServiceIf* /* handler */) {}

 protected:
  boost::shared_ptr<ReverseOrderServiceIf> iface_;
};

class ReverseOrderServiceNull : virtual public ReverseOrderServiceIf {
 public:
  virtual ~ReverseOrderServiceNull() {}
  void myMethod(const std::string& /* first */, const int16_t /* second */, const int32_t /* third */, const int64_t /* fourth */) {
    return;
  }
};

typedef struct _ReverseOrderService_myMethod_args__isset {
  _ReverseOrderService_myMethod_args__isset() : first(false), second(false), third(false), fourth(false) {}
  bool first;
  bool second;
  bool third;
  bool fourth;
} _ReverseOrderService_myMethod_args__isset;

class ReverseOrderService_myMethod_args {
 public:

  ReverseOrderService_myMethod_args() : first(), second(0), third(0), fourth(0) {
  }

  virtual ~ReverseOrderService_myMethod_args() throw() {}

  static ::apache::thrift::reflection::local::TypeSpec* local_reflection;

  std::string first;
  int16_t second;
  int32_t third;
  int64_t fourth;

  _ReverseOrderService_myMethod_args__isset __isset;

  void __set_first(const std::string& val) {
    first = val;
  }

  void __set_second(const int16_t val) {
    second = val;
  }

  void __set_third(const int32_t val) {
    third = val;
  }

  void __set_fourth(const int64_t val) {
    fourth = val;
  }

  bool operator == (const ReverseOrderService_myMethod_args & rhs) const
  {
    if (!(first == rhs.first))
      return false;
    if (!(second == rhs.second))
      return false;
    if (!(third == rhs.third))
      return false;
    if (!(fourth == rhs.fourth))
      return false;
    return true;
  }
  bool operator != (const ReverseOrderService_myMethod_args &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const ReverseOrderService_myMethod_args & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};


class ReverseOrderService_myMethod_pargs {
 public:


  virtual ~ReverseOrderService_myMethod_pargs() throw() {}

  static ::apache::thrift::reflection::local::TypeSpec* local_reflection;

  const std::string* first;
  const int16_t* second;
  const int32_t* third;
  const int64_t* fourth;

  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};


class ReverseOrderService_myMethod_result {
 public:

  ReverseOrderService_myMethod_result() {
  }

  virtual ~ReverseOrderService_myMethod_result() throw() {}

  static ::apache::thrift::reflection::local::TypeSpec* local_reflection;


  bool operator == (const ReverseOrderService_myMethod_result & /* rhs */) const
  {
    return true;
  }
  bool operator != (const ReverseOrderService_myMethod_result &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const ReverseOrderService_myMethod_result & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};


class ReverseOrderService_myMethod_presult {
 public:


  virtual ~ReverseOrderService_myMethod_presult() throw() {}

  static ::apache::thrift::reflection::local::TypeSpec* local_reflection;


  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);

};

class ReverseOrderServiceClient : virtual public ReverseOrderServiceIf {
 public:
  ReverseOrderServiceClient(boost::shared_ptr< ::apache::thrift::protocol::TProtocol> prot) :
    piprot_(prot),
    poprot_(prot) {
    iprot_ = prot.get();
    oprot_ = prot.get();
  }
  ReverseOrderServiceClient(boost::shared_ptr< ::apache::thrift::protocol::TProtocol> iprot, boost::shared_ptr< ::apache::thrift::protocol::TProtocol> oprot) :
    piprot_(iprot),
    poprot_(oprot) {
    iprot_ = iprot.get();
    oprot_ = oprot.get();
  }
  boost::shared_ptr< ::apache::thrift::protocol::TProtocol> getInputProtocol() {
    return piprot_;
  }
  boost::shared_ptr< ::apache::thrift::protocol::TProtocol> getOutputProtocol() {
    return poprot_;
  }
  void myMethod(const std::string& first, const int16_t second, const int32_t third, const int64_t fourth);
  void send_myMethod(const std::string& first, const int16_t second, const int32_t third, const int64_t fourth);
  void recv_myMethod();
 protected:
  boost::shared_ptr< ::apache::thrift::protocol::TProtocol> piprot_;
  boost::shared_ptr< ::apache::thrift::protocol::TProtocol> poprot_;
  ::apache::thrift::protocol::TProtocol* iprot_;
  ::apache::thrift::protocol::TProtocol* oprot_;
};

class ReverseOrderServiceProcessor : public ::apache::thrift::TDispatchProcessor {
 protected:
  boost::shared_ptr<ReverseOrderServiceIf> iface_;
  virtual bool dispatchCall(::apache::thrift::protocol::TProtocol* iprot, ::apache::thrift::protocol::TProtocol* oprot, const std::string& fname, int32_t seqid, void* callContext);
 private:
  typedef  void (ReverseOrderServiceProcessor::*ProcessFunction)(int32_t, ::apache::thrift::protocol::TProtocol*, ::apache::thrift::protocol::TProtocol*, void*);
  typedef std::map<std::string, ProcessFunction> ProcessMap;
  ProcessMap processMap_;
  void process_myMethod(int32_t seqid, ::apache::thrift::protocol::TProtocol* iprot, ::apache::thrift::protocol::TProtocol* oprot, void* callContext);
 public:
  ReverseOrderServiceProcessor(boost::shared_ptr<ReverseOrderServiceIf> iface) :
    iface_(iface) {
    processMap_["myMethod"] = &ReverseOrderServiceProcessor::process_myMethod;
  }

  virtual ~ReverseOrderServiceProcessor() {}
};

class ReverseOrderServiceProcessorFactory : public ::apache::thrift::TProcessorFactory {
 public:
  ReverseOrderServiceProcessorFactory(const ::boost::shared_ptr< ReverseOrderServiceIfFactory >& handlerFactory) :
      handlerFactory_(handlerFactory) {}

  ::boost::shared_ptr< ::apache::thrift::TProcessor > getProcessor(const ::apache::thrift::TConnectionInfo& connInfo);

 protected:
  ::boost::shared_ptr< ReverseOrderServiceIfFactory > handlerFactory_;
};

class ReverseOrderServiceMultiface : virtual public ReverseOrderServiceIf {
 public:
  ReverseOrderServiceMultiface(std::vector<boost::shared_ptr<ReverseOrderServiceIf> >& ifaces) : ifaces_(ifaces) {
  }
  virtual ~ReverseOrderServiceMultiface() {}
 protected:
  std::vector<boost::shared_ptr<ReverseOrderServiceIf> > ifaces_;
  ReverseOrderServiceMultiface() {}
  void add(boost::shared_ptr<ReverseOrderServiceIf> iface) {
    ifaces_.push_back(iface);
  }
 public:
  void myMethod(const std::string& first, const int16_t second, const int32_t third, const int64_t fourth) {
    size_t sz = ifaces_.size();
    size_t i = 0;
    for (; i < (sz - 1); ++i) {
      ifaces_[i]->myMethod(first, second, third, fourth);
    }
    ifaces_[i]->myMethod(first, second, third, fourth);
  }

};

}}} // namespace

#endif
