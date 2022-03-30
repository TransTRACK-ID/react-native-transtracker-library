#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>


@interface RCT_EXTERN_MODULE(TranstrackerLibrary, RCTEventEmitter)

RCT_EXTERN_METHOD(multiply:(float)a withB:(float)b
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(initiateService:apiKey withExternalId:externalId withImei:imei)

RCT_EXTERN_METHOD(startService:
                  (RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(stopService:
                  (RCTResponseSenderBlock)callback)

@end
