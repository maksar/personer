let
  platform = "x86_64-linux";
  flake = (import (fetchTarball {
    url = "https://github.com/edolstra/flake-compat/archive/master.tar.gz";
  }) { src = ./.; });
  pkgs = flake.defaultNix.inputs.nixpkgs.outputs.legacyPackages.${platform};

in {
  ${platform} = pkgs.recurseIntoAttrs {
    personer = flake.defaultNix.defaultPackage.${platform};
  };
}
