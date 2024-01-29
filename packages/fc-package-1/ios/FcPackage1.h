
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNFcPackage1Spec.h"

@interface FcPackage1 : NSObject <NativeFcPackage1Spec>
#else
#import <React/RCTBridgeModule.h>

@interface FcPackage1 : NSObject <RCTBridgeModule>
#endif

@end
