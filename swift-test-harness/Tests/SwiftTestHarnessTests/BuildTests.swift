import XCTest
import IntelMklSrc

final class IntelMklSrcExportTests: XCTestCase {
    func testDefaultConfigurationAndStaticFallback() {
        let configuration = MklBuildConfiguration.Companion.shared.fromFeatures(enabledFeatures: [])
        XCTAssertEqual(configuration.featureName, "mkl-static-ilp64-iomp")
        XCTAssertTrue(configuration.isStatic)
        XCTAssertEqual(
            configuration.fallbackPackage(targetOs: "windows", systemLibraryFound: false),
            "ghcr.io/rust-math/rust-mkl/windows/mkl-static-ilp64-iomp:2022.0-3038006115"
        )
        XCTAssertNil(configuration.fallbackPackage(targetOs: "linux", systemLibraryFound: true))
    }

    func testDynamicConfigurationHasNoFallback() {
        let configuration = MklBuildConfiguration.Companion.shared.fromFeatures(
            enabledFeatures: ["mkl-dynamic-lp64-seq"]
        )
        XCTAssertEqual(configuration.featureName, "mkl-dynamic-lp64-seq")
        XCTAssertFalse(configuration.isStatic)
        XCTAssertNil(configuration.fallbackPackage(targetOs: "linux", systemLibraryFound: false))
    }
}
